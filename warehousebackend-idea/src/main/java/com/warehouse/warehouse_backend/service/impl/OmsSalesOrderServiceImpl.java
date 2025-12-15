package com.warehouse.warehouse_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.warehouse_backend.dto.request.OrderCreateDTO;
import com.warehouse.warehouse_backend.entity.BaseGoods;
import com.warehouse.warehouse_backend.entity.OmsSalesOrder;
import com.warehouse.warehouse_backend.entity.WmsOutboundNote;
import com.warehouse.warehouse_backend.mapper.BaseGoodsMapper;
import com.warehouse.warehouse_backend.mapper.OmsSalesOrderMapper;
import com.warehouse.warehouse_backend.mapper.WmsOutboundNoteMapper; // 引入出库单Mapper
import com.warehouse.warehouse_backend.service.OmsSalesOrderService;
import com.warehouse.warehouse_backend.vo.OmsSalesOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ★必须引入这个
import com.warehouse.warehouse_backend.event.OutboundPendingEvent; // 引入事件
import org.springframework.context.ApplicationEventPublisher; // 引入发布器

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class OmsSalesOrderServiceImpl extends ServiceImpl<OmsSalesOrderMapper, OmsSalesOrder> implements OmsSalesOrderService {
    @Autowired
    private BaseGoodsMapper baseGoodsMapper; // 查货物

    @Autowired
    private WmsOutboundNoteMapper wmsOutboundNoteMapper; // 存出库单

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public IPage<OmsSalesOrderVO> getOrderListWithDetails(int pageNum, int pageSize, Long customerId) {
        Page<OmsSalesOrderVO> page = new Page<>(pageNum, pageSize);
        // 调用 Mapper 的联表查询
        return baseMapper.selectOrderWithDetails(page, customerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrderWithOutbound(OrderCreateDTO dto) {

        // 1. ★ 修改：通过 goodsId 查货物
        BaseGoods goods = baseGoodsMapper.selectById(dto.getGoodsId());

        if (goods == null) {
            throw new RuntimeException("商品ID [" + dto.getGoodsId() + "] 不存在！");
        }

        // ... 2. 生成订单号 (保持不变) ...
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomNum = new Random().nextInt(900) + 100;
        String orderNo = "SO-" + timeStr + "-" + randomNum;

        // 3. 保存销售订单
        OmsSalesOrder order = new OmsSalesOrder();
        order.setOrderNo(orderNo);
        order.setCustomerId(dto.getCustomerId());
        order.setGoodsId(goods.getId());
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setReceiverAddress(dto.getReceiverAddress());
        order.setStatus(0);
        order.setCreateTime(LocalDateTime.now());

        this.save(order);

        // ... 4. 保存出库明细 (保持不变) ...
        WmsOutboundNote outboundNote = new WmsOutboundNote();
        outboundNote.setOrderNo(orderNo);
        outboundNote.setGoodsId(goods.getId());
        outboundNote.setQty(dto.getQty());
        outboundNote.setPickStatus(0);

        wmsOutboundNoteMapper.insert(outboundNote);

        eventPublisher.publishEvent(new OutboundPendingEvent(this, outboundNote));

        return true;
    }
}