package com.warehouse.warehouse_backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.warehouse_backend.common.result.Result;
import com.warehouse.warehouse_backend.entity.SysUser;
import com.warehouse.warehouse_backend.entity.TmsTransportOrder;
import com.warehouse.warehouse_backend.service.SysUserService;
import com.warehouse.warehouse_backend.service.TmsTransportOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transport")
public class TmsTransportOrderController {
    @Autowired private TmsTransportOrderService tmsTransportOrderService;

    @GetMapping("/list")
    public Result<IPage<TmsTransportOrder>> list(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(tmsTransportOrderService.page(new Page<>(pageNum, pageSize)));
    }

    @PostMapping("/sign")
    public Result<Boolean> sign(@RequestParam Long id) {
        try {
            return Result.success(tmsTransportOrderService.signOrder(id));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}