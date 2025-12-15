package com.warehouse.warehouse_backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.warehouse_backend.common.result.Result;
import com.warehouse.warehouse_backend.dto.request.TaskAssignDTO;
import com.warehouse.warehouse_backend.entity.BaseRobot;
import com.warehouse.warehouse_backend.entity.WmsRobotTask;
import com.warehouse.warehouse_backend.service.WmsRobotTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.warehouse.warehouse_backend.vo.RobotTaskVO;

import java.util.List;

@RestController
@RequestMapping("/task")
public class WmsRobotTaskController {

    @Autowired
    private WmsRobotTaskService wmsRobotTaskService;

    /**
     * 1. 任务列表查询接口 (已修复 404/数据不全问题)
     * GET /task/list?pageNum=1&pageSize=10
     */
    @GetMapping("/list")
    public Result<IPage<RobotTaskVO>> list( // ★ 修复点 1: 返回类型改为 IPage<RobotTaskVO>
                                            @RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize) {

        // ★ 修复点 2: 调用 Service 中自定义的联表查询方法
        IPage<RobotTaskVO> data = wmsRobotTaskService.getTaskWithDetails(pageNum, pageSize);
        return Result.success(data);
    }

    /**
     * 2. 完成任务接口
     * POST /task/complete?taskId=1
     */
    @PostMapping("/complete")
    public Result<Boolean> completeTask(@RequestParam Long taskId) {
        try {
            return Result.success(wmsRobotTaskService.completeTask(taskId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/robots")
    public Result<List<BaseRobot>> getAllRobots() {
        List<BaseRobot> robots = wmsRobotTaskService.getAllRobots();
        return Result.success(robots);
    }

    @PostMapping("/assign")
    public Result<Boolean> assignRobot(@RequestBody TaskAssignDTO dto) {
        try {
            return Result.success(wmsRobotTaskService.assignRobot(dto));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

}