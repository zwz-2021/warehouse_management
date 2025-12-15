package com.warehouse.warehouse_backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.warehouse_backend.common.result.Result;
import com.warehouse.warehouse_backend.entity.WmsInboundNote;
import com.warehouse.warehouse_backend.service.WmsInboundNoteService;
import com.warehouse.warehouse_backend.vo.InboundNoteVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.warehouse.warehouse_backend.dto.request.InboundCreateDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/inbound")
public class WmsInboundNoteController {
    @Autowired private WmsInboundNoteService wmsInboundNoteService;

    @GetMapping("/list")
    public Result<IPage<InboundNoteVO>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        // 调用新的联表查询方法
        IPage<InboundNoteVO> data = wmsInboundNoteService.getInboundWithDetails(pageNum, pageSize);
        return Result.success(data);
    }

    /**
     * 直接入库（通过名称）
     * 请求方式: POST
     * URL: http://localhost:8080/inbound/add-direct
     */
    @PostMapping("/add-direct")
    public Result<Boolean> addDirect(@RequestBody InboundCreateDTO dto) {
        try {
            boolean success = wmsInboundNoteService.createDirectInbound(dto);
            return Result.success(success);
        } catch (Exception e) {
            // 如果找不到货物，会抛出异常，这里捕获并返回错误信息
            return Result.error(e.getMessage());
        }
    }
}