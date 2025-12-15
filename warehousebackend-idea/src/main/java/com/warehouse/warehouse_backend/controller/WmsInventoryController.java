package com.warehouse.warehouse_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.warehouse_backend.common.result.Result;
import com.warehouse.warehouse_backend.entity.WmsInventory;
import com.warehouse.warehouse_backend.mapper.WmsInventoryMapper;
import com.warehouse.warehouse_backend.service.WmsInventoryService;
import com.warehouse.warehouse_backend.vo.InventoryDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class WmsInventoryController {
    @Autowired private WmsInventoryService wmsInventoryService;

    @Autowired
    private WmsInventoryMapper wmsInventoryMapper;

    /**
     * 库存查询接口 (使用自定义联表查询，返回名称和编码)
     */
    @GetMapping("/list")
    public Result<IPage<InventoryDetailVO>> list( // ★ 返回类型改为 IPage<InventoryDetailVO>
                                                  @RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  @RequestParam(required = false) Long goodsId,
                                                  @RequestParam(required = false) Long locationId
    ) {
        // 1. 使用 Page<InventoryDetailVO> 作为分页对象
        Page<InventoryDetailVO> page = new Page<>(pageNum, pageSize);

        // 2. 调用 Mapper 的联表查询方法
        IPage<InventoryDetailVO> result = wmsInventoryMapper.selectInventoryDetail(
                page,
                goodsId,
                locationId
        );

        return Result.success(result);
    }
}