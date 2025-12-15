package com.warehouse.warehouse_backend.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.warehouse.warehouse_backend.entity.*;
import com.warehouse.warehouse_backend.event.TaskCompletedEvent;
import com.warehouse.warehouse_backend.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
public class OutboundCompletionListener {

    @Autowired
    private WmsOutboundNoteMapper outboundNoteMapper;
    @Autowired
    private WmsInventoryMapper inventoryMapper;
    @Autowired
    private OmsSalesOrderMapper salesOrderMapper;
    @Autowired
    private TmsTransportOrderMapper transportOrderMapper;

    /**
     * 监听任务完成事件 (专门处理 OUTBOUND 拣选任务)
     */
    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void handleOutboundComplete(TaskCompletedEvent event) {
        WmsRobotTask task = event.getTask();

        // ★ 只处理“拣选任务” (OUTBOUND)
        if ("OUTBOUND".equals(task.getTaskType())) {
            System.out.println(">>> 监听到拣选任务完成，任务ID: " + task.getId() + "，开始执行出库后置逻辑...");
            processOutbound(task);
        }
    }

    private void processOutbound(WmsRobotTask task) {
        // 获取出库单明细
        WmsOutboundNote note = outboundNoteMapper.selectById(task.getSourceDocId());
        if (note == null) return;
        // 扣减库存
        LambdaQueryWrapper<WmsInventory> invWrapper = new LambdaQueryWrapper<>();
        invWrapper.eq(WmsInventory::getGoodsId, note.getGoodsId());
        invWrapper.eq(WmsInventory::getLocationId, task.getTargetLocationId());
        WmsInventory inventory = inventoryMapper.selectOne(invWrapper);
        if (inventory != null) {
            // 扣减总库存
            int newQty = inventory.getTotalQty() - note.getQty();
            if (newQty < 0) newQty = 0; // 防止负库存
            inventory.setTotalQty(newQty);
            // 锁定库存(lockedQty)，这里也要对应扣减，这里暂时只减总数
            int currentLocked = inventory.getLockedQty() == null ? 0 : inventory.getLockedQty();
            int newLocked = currentLocked - note.getQty();
            inventory.setLockedQty(Math.max(newLocked, 0)); // 防止负数
            inventory.setUpdateTime(LocalDateTime.now());
            inventoryMapper.updateById(inventory);
            System.out.println(">>> 库存扣减成功，剩余库存: " + newQty);
        }


        // 更新出库单状态 -> 已拣选(1)

        note.setPickStatus(2);
        outboundNoteMapper.updateById(note);


        //更新销售订单状态 -> 已发货(3)

        // 根据 order_no 找到主订单
        LambdaQueryWrapper<OmsSalesOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(OmsSalesOrder::getOrderNo, note.getOrderNo());
        OmsSalesOrder order = salesOrderMapper.selectOne(orderWrapper);

        if (order != null) {
            order.setStatus(3); // 3-已发货
            salesOrderMapper.updateById(order);
            System.out.println(">>> 销售订单状态已更新为：已发货");
        }


        // 生成物流运单 (TMS)
        createTransportOrder(note.getOrderNo());
    }

    private void createTransportOrder(String orderNo) {
        // 生成运单号: SF + 时间戳 + 随机数
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String trackingNo = "SF" + timeStr + (new Random().nextInt(900) + 100);

        TmsTransportOrder transport = new TmsTransportOrder();
        transport.setTrackingNo(trackingNo);
        transport.setOrderNo(orderNo);
        transport.setLogisticsCompany("顺丰速运"); // 默认物流
        transport.setStatus("TRANSIT"); // 运输中
        transport.setCreateTime(LocalDateTime.now());

        transportOrderMapper.insert(transport);
        System.out.println(">>> 物流运单生成成功！单号: " + trackingNo);
    }
}