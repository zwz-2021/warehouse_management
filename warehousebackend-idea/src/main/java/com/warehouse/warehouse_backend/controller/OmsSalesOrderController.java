package com.warehouse.warehouse_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.warehouse_backend.common.result.Result;
import com.warehouse.warehouse_backend.entity.OmsSalesOrder;
import com.warehouse.warehouse_backend.entity.SysUser;
import com.warehouse.warehouse_backend.service.OmsSalesOrderService;
import com.warehouse.warehouse_backend.vo.OmsSalesOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.warehouse.warehouse_backend.dto.request.OrderCreateDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/order")
public class OmsSalesOrderController {
    @Autowired private OmsSalesOrderService omsSalesOrderService;

    @GetMapping("/list")
    public Result<IPage<OmsSalesOrderVO>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long customerId
    ) {
        // 直接调用带详情的查询
        IPage<OmsSalesOrderVO> page = omsSalesOrderService.getOrderListWithDetails(pageNum, pageSize, customerId);
        return Result.success(page);
    }

    /**
     * 下单接口：自动生成订单 + 出库单
     * POST http://localhost:8080/order/create
     */
    @PostMapping("/create")
    public Result<Boolean> createOrder(@RequestBody OrderCreateDTO dto) {
        try {
            boolean success = omsSalesOrderService.createOrderWithOutbound(dto);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}