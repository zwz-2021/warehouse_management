package com.warehouse.warehouse_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.warehouse_backend.entity.WmsRobotTask;
import com.warehouse.warehouse_backend.mapper.BaseRobotMapper;
import com.warehouse.warehouse_backend.mapper.WmsRobotTaskMapper;
import com.warehouse.warehouse_backend.service.WmsRobotTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.warehouse_backend.vo.RobotTaskVO;
import com.warehouse.warehouse_backend.dto.request.TaskAssignDTO;
import com.warehouse.warehouse_backend.entity.BaseRobot;
import org.springframework.transaction.annotation.Transactional;
import com.warehouse.warehouse_backend.event.TaskCompletedEvent; // 引入事件
import com.warehouse.warehouse_backend.entity.BaseRobot; // 引入机器人实体
import com.warehouse.warehouse_backend.mapper.BaseRobotMapper; // 引入机器人Mapper
import org.springframework.context.ApplicationEventPublisher; // 引入发布器

import java.util.List;

@Service
public class WmsRobotTaskServiceImpl extends ServiceImpl<WmsRobotTaskMapper, WmsRobotTask> implements WmsRobotTaskService {
    @Autowired
    private WmsRobotTaskMapper robotTaskMapper;

    @Autowired
    private BaseRobotMapper baseRobotMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public IPage<RobotTaskVO> getTaskWithDetails(int pageNum, int pageSize) {
        Page<RobotTaskVO> page = new Page<>(pageNum, pageSize);
        return robotTaskMapper.selectTaskWithDetails(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // ★ 开启事务：同生共死
    public boolean assignRobot(TaskAssignDTO dto) {

        // 1. 检查任务是否存在 & 状态是否正确
        WmsRobotTask task = this.getById(dto.getTaskId());
        if (task == null) {
            throw new RuntimeException("任务不存在！");
        }
        if (!"PENDING".equals(task.getStatus())) {
            throw new RuntimeException("该任务不是[待分配]状态，无法分配！");
        }

        // 2. 检查机器人是否存在 & 是否空闲
        BaseRobot robot = baseRobotMapper.selectById(dto.getRobotId());
        if (robot == null) {
            throw new RuntimeException("机器人不存在！");
        }
        if (!"IDLE".equals(robot.getStatus())) {
            throw new RuntimeException("该机器人正在忙碌或故障，请选择其他机器人！");
        }

        // 3. 更新任务状态
        task.setRobotId(dto.getRobotId());
        task.setStatus("ASSIGNED"); // 变更为：已分配/待执行
        this.updateById(task);

        // 4. 更新机器人状态
        robot.setStatus("BUSY"); // 变更为：忙碌
        baseRobotMapper.updateById(robot);

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean completeTask(Long taskId) {
        // 1. 查任务
        WmsRobotTask task = this.getById(taskId);
        if (task == null) throw new RuntimeException("任务不存在");

        // 2. 更新任务状态
        task.setStatus("COMPLETED");
        this.updateById(task);

        // 3. 释放机器人 (状态变回 IDLE)
        if (task.getRobotId() != null) {
            BaseRobot robot = baseRobotMapper.selectById(task.getRobotId());
            if (robot != null) {
                robot.setStatus("IDLE");
                baseRobotMapper.updateById(robot);
            }
        }

        // 4. 发布任务完成事件
        eventPublisher.publishEvent(new TaskCompletedEvent(this, task));

        return true;
    }

    @Override
    public List<BaseRobot> getAllRobots() {
        // 直接查询 base_robot 表的所有记录
        return baseRobotMapper.selectList(null);
    }
}