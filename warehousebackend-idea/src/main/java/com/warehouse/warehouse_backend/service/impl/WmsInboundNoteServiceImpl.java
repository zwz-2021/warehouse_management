package com.warehouse.warehouse_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.warehouse_backend.dto.request.InboundCreateDTO;
import com.warehouse.warehouse_backend.entity.BaseGoods;
import com.warehouse.warehouse_backend.entity.BaseLocation;
import com.warehouse.warehouse_backend.entity.WmsInboundNote;
import com.warehouse.warehouse_backend.entity.WmsInventory;
import com.warehouse.warehouse_backend.event.InboundPendingEvent;
import com.warehouse.warehouse_backend.mapper.BaseGoodsMapper;
import com.warehouse.warehouse_backend.mapper.BaseLocationMapper;
import com.warehouse.warehouse_backend.mapper.WmsInboundNoteMapper;
import com.warehouse.warehouse_backend.mapper.WmsInventoryMapper;
import com.warehouse.warehouse_backend.service.WmsInboundNoteService;
import com.warehouse.warehouse_backend.vo.InboundNoteVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WmsInboundNoteServiceImpl extends ServiceImpl<WmsInboundNoteMapper, WmsInboundNote> implements WmsInboundNoteService {

    @Autowired
    private BaseGoodsMapper baseGoodsMapper;

    @Autowired
    private BaseLocationMapper baseLocationMapper; // 新增注入

    @Autowired
    private WmsInventoryMapper wmsInventoryMapper; // 新增注入

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * 自动分配空货位算法
     * 逻辑：查询所有货位，排除掉在库存表中有记录且数量大于0的货位
     */
    private Long smartAllocateLocation(Long goodsId) {
        // --- 策略 1: 查找已存放相同货物的货位 (合并库存) ---
        LambdaQueryWrapper<WmsInventory> sameGoodsQuery = new LambdaQueryWrapper<>();
        sameGoodsQuery.eq(WmsInventory::getGoodsId, goodsId)
                .gt(WmsInventory::getTotalQty, 0) // 确保该货位真的有货
                .last("LIMIT 1"); // 只需要找到一个即可
        WmsInventory existingInventory = wmsInventoryMapper.selectOne(sameGoodsQuery);

        if (existingInventory != null) {
            // 找到了！直接返回这个旧货位的ID
            return existingInventory.getLocationId();
        }

        // --- 策略 2: 查找完全空闲的货位 ---
        // 2.1 查出所有“忙碌”的货位ID (有库存记录且数量>0)
        LambdaQueryWrapper<WmsInventory> busyQuery = new LambdaQueryWrapper<>();
        busyQuery.gt(WmsInventory::getTotalQty, 0);
        List<WmsInventory> busyInventories = wmsInventoryMapper.selectList(busyQuery);

        List<Long> busyLocationIds = busyInventories.stream()
                .map(WmsInventory::getLocationId)
                .collect(Collectors.toList());

        // 2.2 在 BaseLocation 中查找不在 busyLocationIds 里的货位
        LambdaQueryWrapper<BaseLocation> freeQuery = new LambdaQueryWrapper<>();
        freeQuery.eq(BaseLocation::getStatus, 0); // 必须是状态正常的货位

        if (!busyLocationIds.isEmpty()) {
            freeQuery.notIn(BaseLocation::getId, busyLocationIds);
        }

        freeQuery.last("LIMIT 1"); // 只取一个

        BaseLocation freeLocation = baseLocationMapper.selectOne(freeQuery);

        if (freeLocation != null) {
            return freeLocation.getId();
        }

        // --- 策略 3: 彻底失败 ---
        throw new RuntimeException("分配失败：仓库已满，没有存放该货物的旧货位，也没有空货位！");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDirectInbound(InboundCreateDTO dto) {
        // 1. 校验货物ID是否存在
        BaseGoods goods = baseGoodsMapper.selectById(dto.getGoodsId());
        if (goods == null) {
            throw new RuntimeException("货物ID不存在，请刷新页面后重试！");
        }

        // 2. 构建入库单实体
        WmsInboundNote note = new WmsInboundNote();
        note.setGoodsId(dto.getGoodsId());
        note.setActualQty(dto.getActualQty());
        note.setPoNo("DIRECT_INBOUND");
        note.setPlanQty(0); // 数据库非空字段填充

        note.setAssignMethod(dto.getAssignMethod() != null ? dto.getAssignMethod() : 1);

        // 3. 处理质检状态和上架状态
        if (dto.getQcStatus() != null) {
            note.setQcStatus(dto.getQcStatus());
        } else {
            note.setQcStatus(2); // 默认不合格
        }

        // 状态逻辑修改：
        // 1-合格 -> 尝试分配货位 -> 设置为待上架(1)
        // 2-不合格 -> 不分配货位 -> 设置为不上架(0 或 自定义状态)
        if (note.getQcStatus() == 1) {
            // ★ 自动分配货位
            Long targetLocationId = smartAllocateLocation(dto.getGoodsId());
            note.setTargetLocationId(targetLocationId);
            note.setPutawayStatus(1); // 1-待上架 (等待机器人搬运)
        } else {
            // 不合格，不上架
            note.setTargetLocationId(null);
            note.setPutawayStatus(0); // 0-不上架/待处理
        }

        // 保存入库单
        boolean success = this.save(note);

        // 4. 触发事件逻辑 (只有合格且待上架才触发)
        if (success && note.getQcStatus() == 1 && note.getPutawayStatus() == 1) {
            // 发布事件，通知机器人模块生成任务
            eventPublisher.publishEvent(new InboundPendingEvent(this, note));
        }

        return success;
    }

    @Override
    public IPage<InboundNoteVO> getInboundWithDetails(int pageNum, int pageSize) {
        Page<InboundNoteVO> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectInboundWithDetails(page); // baseMapper 就是 WmsInboundNoteMapper
    }
}