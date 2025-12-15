package com.warehouse.warehouse_backend.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.warehouse.warehouse_backend.entity.BaseLocation;
import com.warehouse.warehouse_backend.entity.WmsInboundNote;
import com.warehouse.warehouse_backend.entity.WmsInventory;
import com.warehouse.warehouse_backend.entity.WmsRobotTask;
import com.warehouse.warehouse_backend.event.TaskCompletedEvent;
import com.warehouse.warehouse_backend.mapper.BaseLocationMapper;
import com.warehouse.warehouse_backend.mapper.WmsInboundNoteMapper;
import com.warehouse.warehouse_backend.mapper.WmsInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class InventoryUpdateListener {

    @Autowired
    private WmsInventoryMapper inventoryMapper;
    @Autowired
    private WmsInboundNoteMapper inboundNoteMapper;
    @Autowired
    private BaseLocationMapper locationMapper;

    /**
     * 监听任务完成事件
     */
    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void handleTaskComplete(TaskCompletedEvent event) {
        WmsRobotTask task = event.getTask();

        // ★ 只处理“上架任务” (INBOUND)
        if ("INBOUND".equals(task.getTaskType())) {
            System.out.println(">>> 监听到上架任务完成，任务ID: " + task.getId() + "，准备增加库存...");
            updateInventoryForInbound(task);
        }
    }

    // 具体的加库存逻辑
    private void updateInventoryForInbound(WmsRobotTask task) {
        // 1. 查入库单 (获取实际上架数量 actual_qty)
        WmsInboundNote note = inboundNoteMapper.selectById(task.getSourceDocId());
        if (note == null) return;

        // 2. 查该货位上是否已经有这种货了
        LambdaQueryWrapper<WmsInventory> query = new LambdaQueryWrapper<>();
        query.eq(WmsInventory::getGoodsId, note.getGoodsId());
        query.eq(WmsInventory::getLocationId, task.getTargetLocationId());

        WmsInventory inventory = inventoryMapper.selectOne(query);

        if (inventory != null) {
            // A. 如果有，直接在原有数量上累加
            inventory.setTotalQty(inventory.getTotalQty() + note.getActualQty());
            inventory.setUpdateTime(LocalDateTime.now());
            inventoryMapper.updateById(inventory);
            System.out.println(">>>原有库存更新，当前总数: " + inventory.getTotalQty());
        } else {
            // B. 如果没有，新增一条库存记录
            inventory = new WmsInventory();
            inventory.setGoodsId(note.getGoodsId());
            inventory.setLocationId(task.getTargetLocationId());
            inventory.setTotalQty(note.getActualQty());
            inventory.setLockedQty(0); // 初始锁定为0
            inventory.setUpdateTime(LocalDateTime.now());
            inventoryMapper.insert(inventory);
            System.out.println(">>> 新增库存记录，数量: " + note.getActualQty());
        }

        // 3. 更新入库单状态为“已上架/已完成” (2)
        note.setPutawayStatus(2);
        inboundNoteMapper.updateById(note);

        // 4. 更新货位状态为“占用” (1)
        BaseLocation location = locationMapper.selectById(task.getTargetLocationId());
        if (location != null && location.getStatus() == 0) {
            location.setStatus(1); // 设为占用
            locationMapper.updateById(location);
        }
    }
}