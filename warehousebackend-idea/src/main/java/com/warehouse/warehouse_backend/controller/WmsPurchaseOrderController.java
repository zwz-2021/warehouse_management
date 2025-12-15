package com.warehouse.warehouse_backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.warehouse_backend.common.result.Result;
import com.warehouse.warehouse_backend.entity.WmsPurchaseOrder;
import com.warehouse.warehouse_backend.service.WmsPurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchase")
public class WmsPurchaseOrderController {
    @Autowired private WmsPurchaseOrderService wmsPurchaseOrderService;

    @GetMapping("/list")
    public Result<IPage<WmsPurchaseOrder>> list(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(wmsPurchaseOrderService.page(new Page<>(pageNum, pageSize)));
    }
}