package com.warehouse.warehouse_backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.warehouse_backend.common.result.Result;
import com.warehouse.warehouse_backend.entity.WmsOutboundNote;
import com.warehouse.warehouse_backend.service.WmsOutboundNoteService;
import com.warehouse.warehouse_backend.vo.WmsOutboundNoteVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/outbound")
public class WmsOutboundNoteController {
    @Autowired
    private WmsOutboundNoteService outboundNoteService;

    @GetMapping("/list")
    public Result<IPage<WmsOutboundNoteVO>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Result.success(outboundNoteService.getOutboundListWithDetails(pageNum, pageSize));
    }
}