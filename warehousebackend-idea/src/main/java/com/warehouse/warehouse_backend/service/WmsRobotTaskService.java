package com.warehouse.warehouse_backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.warehouse.warehouse_backend.entity.BaseRobot;
import com.warehouse.warehouse_backend.entity.WmsRobotTask;
import com.warehouse.warehouse_backend.vo.RobotTaskVO;
import com.warehouse.warehouse_backend.dto.request.TaskAssignDTO;

import java.util.List;

public interface WmsRobotTaskService extends IService<WmsRobotTask> {
    IPage<RobotTaskVO> getTaskWithDetails(int pageNum, int pageSize);
    boolean assignRobot(TaskAssignDTO dto);
    boolean completeTask(Long taskId);
    List<BaseRobot> getAllRobots();
}