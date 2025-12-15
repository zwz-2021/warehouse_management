package com.warehouse.warehouse_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.warehouse.warehouse_backend.common.result.Result;
import com.warehouse.warehouse_backend.entity.BaseLocation;
import com.warehouse.warehouse_backend.service.BaseLocationService;
import com.warehouse.warehouse_backend.vo.LocationSensorVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/location")
public class BaseLocationController {

    @Autowired
    private BaseLocationService baseLocationService;

    /**
     * 1. 货位列表查询 (带传感器数据的完整列表)
     * GET /location/list
     */
    @GetMapping("/list")
    public Result<IPage<LocationSensorVO>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String locationCode) {

        // 调用 Service 中联表查询传感器数据的方法
        IPage<LocationSensorVO> data = baseLocationService.getLocationWithSensor(pageNum, pageSize, locationCode);
        return Result.success(data);
    }

    /**
     * 2. 新增货位 (已修复: 调用自定义的初始化传感器数据方法)
     * POST /location/add
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody BaseLocation location) {
        // 检查货位编码是否重复
        LambdaQueryWrapper<BaseLocation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseLocation::getLocationCode, location.getLocationCode());
        if (baseLocationService.count(wrapper) > 0) {
            return Result.error("货位编码 [" + location.getLocationCode() + "] 已存在");
        }

        // 默认状态为 0-空闲
        if (location.getStatus() == null) {
            location.setStatus(0);
        }

        // ★ 关键修复点：调用 Service 中自定义的 addLocationAndInitSensorData 方法
        baseLocationService.addLocationAndInitSensorData(location);

        return Result.success("新增货位成功");
    }

    /**
     * 3. 删除货位 (调用带库存检查的方法，这部分也需要修复)
     * DELETE /location/delete/{id}
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        // ★ 关键修复点：调用 Service 中自定义的 deleteLocationWithInventoryCheck 方法
        boolean success = baseLocationService.deleteLocationWithInventoryCheck(id);

        if (success) {
            return Result.success("货位删除成功");
        } else {
            // ServiceImpl 返回 false，说明货位非空
            return Result.error("删除失败：该货位下仍存在库存，禁止删除！");
        }
    }
}