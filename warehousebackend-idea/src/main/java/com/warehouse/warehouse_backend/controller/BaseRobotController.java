package com.warehouse.warehouse_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.warehouse_backend.common.result.Result;
import com.warehouse.warehouse_backend.entity.BaseRobot;
import com.warehouse.warehouse_backend.service.BaseRobotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/robot")
public class BaseRobotController {
    @Autowired private BaseRobotService baseRobotService;

    @GetMapping("/list")
    public Result<IPage<BaseRobot>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status // 状态参数
    ) {
        Page<BaseRobot> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<BaseRobot> wrapper = new LambdaQueryWrapper<>();
        // 如果前端传了 status (比如 IDLE)，就拼接到 SQL WHERE 条件里
        if (status != null && !status.isEmpty()) {
            wrapper.eq(BaseRobot::getStatus, status);
        }

        return Result.success(baseRobotService.page(page, wrapper));
    }
}