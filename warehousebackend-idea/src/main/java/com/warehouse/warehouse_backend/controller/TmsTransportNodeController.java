package com.warehouse.warehouse_backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.warehouse_backend.common.result.Result;
import com.warehouse.warehouse_backend.entity.TmsTransportNode;
import com.warehouse.warehouse_backend.service.TmsTransportNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transport-node")
public class TmsTransportNodeController {
    @Autowired private TmsTransportNodeService tmsTransportNodeService;

    @GetMapping("/list")
    public Result<IPage<TmsTransportNode>> list(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(tmsTransportNodeService.page(new Page<>(pageNum, pageSize)));
    }
}