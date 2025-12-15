package com.warehouse.warehouse_backend.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.warehouse.warehouse_backend.entity.*;
import com.warehouse.warehouse_backend.event.InboundPendingEvent;
import com.warehouse.warehouse_backend.event.OutboundPendingEvent;
import com.warehouse.warehouse_backend.mapper.BaseRobotMapper; // ★ 引入机器人Mapper
import com.warehouse.warehouse_backend.mapper.WmsInventoryMapper;
import com.warehouse.warehouse_backend.mapper.WmsRobotTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
public class RobotTaskListener {

    @Autowired
    private WmsRobotTaskMapper robotTaskMapper;

    @Autowired
    private WmsInventoryMapper wmsInventoryMapper;

    @Autowired
    private BaseRobotMapper baseRobotMapper; // ★ 新增注入，用于查询空闲机器人

    /**
     * 监听入库事件
     */
    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void handleInboundEvent(InboundPendingEvent event) {
        WmsInboundNote note = event.getInboundNote();
        System.out.println(">>> 监听到入库事件，入库单ID: " + note.getId() + "，准备生成机器人任务...");

        // 1. 生成任务编号
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        String taskNo = "T-IN-" + timeStr + "-" + (new Random().nextInt(900) + 100);

        // 2. 构建机器人任务基础信息
        WmsRobotTask task = new WmsRobotTask();
        task.setTaskNo(taskNo);
        task.setTaskType("INBOUND");
        task.setSourceDocId(note.getId());
        task.setTargetLocationId(note.getTargetLocationId());
        task.setCreateTime(LocalDateTime.now());

        // ★★★ 核心逻辑：判断分配方式 ★★★
        boolean assigned = false;

        // 如果用户选择了“自动分配” (假设 2 代表自动)
        if (note.getAssignMethod() != null && note.getAssignMethod() == 2) {
            System.out.println(">>> 用户选择[自动分配]，正在寻找空闲机器人...");

            // 查找一个状态为 IDLE (空闲) 的机器人
            LambdaQueryWrapper<BaseRobot> robotQuery = new LambdaQueryWrapper<>();
            robotQuery.eq(BaseRobot::getStatus, "IDLE");
            robotQuery.last("LIMIT 1"); // 只取一个

            BaseRobot idleRobot = baseRobotMapper.selectOne(robotQuery);

            if (idleRobot != null) {
                // 找到机器人了！
                task.setRobotId(idleRobot.getId());
                task.setStatus("ASSIGNED"); // 状态直接变为：已分配

                // ★ 重要：立即更新机器人状态为 BUSY，防止被其他任务抢占
                idleRobot.setStatus("BUSY");
                baseRobotMapper.updateById(idleRobot);

                assigned = true;
                System.out.println(">>> 自动分配成功！机器人: " + idleRobot.getRobotName());
            } else {
                System.out.println(">>> 自动分配失败：当前没有空闲机器人，转为[手动分配]模式。");
            }
        }

        // 如果没选择自动，或者自动分配失败，则默认为手动模式
        if (!assigned) {
            task.setStatus("PENDING"); // 状态：待分配
            task.setRobotId(null);     // 空置
            System.out.println(">>> 任务模式：手动分配");
        }

        // 3. 保存任务
        robotTaskMapper.insert(task);
        System.out.println(">>> 机器人上架任务已保存！任务号: " + taskNo);
    }

    /**
     * 监听出库事件
     */
    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void handleOutboundEvent(OutboundPendingEvent event) {
        WmsOutboundNote note = event.getOutboundNote();

        LambdaQueryWrapper<WmsInventory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WmsInventory::getGoodsId, note.getGoodsId());
        queryWrapper.ge(WmsInventory::getTotalQty, note.getQty());
        queryWrapper.last("LIMIT 1");

        WmsInventory inventory = wmsInventoryMapper.selectOne(queryWrapper);

        if (inventory == null) {
            System.err.println(">>> 库存不足，无法生成任务！");
            return;
        }

        int currentLocked = inventory.getLockedQty() == null ? 0 : inventory.getLockedQty();
        inventory.setLockedQty(currentLocked + note.getQty());
        wmsInventoryMapper.updateById(inventory); // 更新数据库
        System.out.println(">>> 库存已锁定，锁定数量: " + note.getQty());

        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        String taskNo = "T-OUT-" + timeStr + "-" + (new Random().nextInt(900) + 100);

        WmsRobotTask task = new WmsRobotTask();
        task.setTaskNo(taskNo);
        task.setTaskType("OUTBOUND");
        task.setSourceDocId(note.getId());
        task.setTargetLocationId(inventory.getLocationId());
        task.setCreateTime(LocalDateTime.now());
        boolean assigned = false;

        // 自动分配
        System.out.println(">>> 用户选择[自动分配]，正在寻找空闲机器人...");
        // 查找一个状态为 IDLE (空闲) 的机器人
        LambdaQueryWrapper<BaseRobot> robotQuery = new LambdaQueryWrapper<>();
        robotQuery.eq(BaseRobot::getStatus, "IDLE");
        robotQuery.last("LIMIT 1"); // 只取一个

        BaseRobot idleRobot = baseRobotMapper.selectOne(robotQuery);

        if (idleRobot != null) {
            // 找到机器人了！
            task.setRobotId(idleRobot.getId());
            task.setStatus("ASSIGNED"); // 状态直接变为：已分配
            // ★ 重要：立即更新机器人状态为 BUSY，防止被其他任务抢占
            idleRobot.setStatus("BUSY");
            baseRobotMapper.updateById(idleRobot);

            note.setPickStatus(0);

            assigned = true;
            System.out.println(">>> 自动分配成功！机器人: " + idleRobot.getRobotName());
        } else {
            System.out.println(">>> 自动分配失败：当前没有空闲机器人，转为[手动分配]模式。");
        }


        // 如果没选择自动，或者自动分配失败，则默认为手动模式
        if (!assigned) {
            task.setStatus("PENDING"); // 状态：待分配
            task.setRobotId(null);     // 空置
            System.out.println(">>> 任务模式：手动分配");
        }

        // 3. 保存任务
        robotTaskMapper.insert(task);
        System.out.println(">>> 机器人拣选任务已保存！任务号: " + taskNo);
    }
}