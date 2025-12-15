package com.warehouse.warehouse_backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.warehouse.warehouse_backend.entity.BaseLocation;
import com.warehouse.warehouse_backend.vo.LocationSensorVO;

public interface BaseLocationService extends IService<BaseLocation> {
    // 定义一个获取组合数据的方法
    IPage<LocationSensorVO> getLocationWithSensor(int pageNum, int pageSize, String locationCode);

    boolean addLocationAndInitSensorData(BaseLocation location);
    // 假设你还需要这个 CRUD 的删除方法
    boolean deleteLocationWithInventoryCheck(Long id);
}