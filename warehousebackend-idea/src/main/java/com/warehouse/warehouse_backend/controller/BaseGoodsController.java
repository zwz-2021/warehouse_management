package com.warehouse.warehouse_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.warehouse_backend.common.result.Result;
import com.warehouse.warehouse_backend.entity.BaseGoods;
import com.warehouse.warehouse_backend.service.BaseGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goods")
public class BaseGoodsController {
    @Autowired private BaseGoodsService baseGoodsService;

    // 1. 查询接口 (支持模糊搜索)
    @GetMapping("/list")
    public Result<IPage<BaseGoods>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name // 前端传来的搜索关键词
    ) {
        Page<BaseGoods> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BaseGoods> wrapper = new LambdaQueryWrapper<>();

        // 如果 name 有值，就去匹配 商品名 或 编码
        if (StringUtils.hasText(name)) {
            wrapper.and(w -> w.like(BaseGoods::getGoodsName, name)
                    .or()
                    .like(BaseGoods::getGoodsCode, name));
        }

        // 按ID倒序排列
        wrapper.orderByDesc(BaseGoods::getId);

        return Result.success(baseGoodsService.page(page, wrapper));
    }

    // 2. 新增接口
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody BaseGoods baseGoods) {
        // 简单校验：编码不能重复
        LambdaQueryWrapper<BaseGoods> query = new LambdaQueryWrapper<>();
        query.eq(BaseGoods::getGoodsCode, baseGoods.getGoodsCode());
        if (baseGoodsService.count(query) > 0) {
            return Result.error("货物编码 [" + baseGoods.getGoodsCode() + "] 已存在！");
        }
        return Result.success(baseGoodsService.save(baseGoods));
    }

    // 3. 删除接口
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(baseGoodsService.removeById(id));
    }

    @GetMapping("/all")
    public Result<List<BaseGoods>> getAllGoods() {
        // 使用 MyBatis-Plus 提供的 list() 方法查询所有数据
        List<BaseGoods> list = baseGoodsService.list();
        return Result.success(list);
    }
}