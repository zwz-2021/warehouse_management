package com.warehouse.warehouse_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.warehouse_backend.entity.BaseLocation;
import com.warehouse.warehouse_backend.entity.IotSensorData;
import com.warehouse.warehouse_backend.entity.WmsInventory;
import com.warehouse.warehouse_backend.mapper.BaseLocationMapper;
import com.warehouse.warehouse_backend.mapper.IotSensorDataMapper;
import com.warehouse.warehouse_backend.mapper.OmsSalesOrderMapper;
import com.warehouse.warehouse_backend.mapper.WmsInventoryMapper;
import com.warehouse.warehouse_backend.service.BaseLocationService;
import com.warehouse.warehouse_backend.vo.LocationSensorVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
public class BaseLocationServiceImpl extends ServiceImpl<BaseLocationMapper, BaseLocation> implements BaseLocationService {

    @Autowired
    private BaseLocationMapper baseLocationMapper;

    @Autowired
    private WmsInventoryMapper wmsInventoryMapper;

    @Autowired
    private IotSensorDataMapper iotSensorDataMapper;

    @Override
    public IPage<LocationSensorVO> getLocationWithSensor(int pageNum, int pageSize, String locationCode) {
        // 创建分页对象
        Page<LocationSensorVO> page = new Page<>(pageNum, pageSize);
        // 调用 Mapper 自定义的方法，传入搜索参数
        return baseLocationMapper.selectLocationWithSensor(page, locationCode);
    }

    @Override
    @Transactional
    public boolean deleteLocationWithInventoryCheck(Long id) {
        // 1. 检查 wms_inventory 表中是否有该 locationId 的记录，且库存数量 (总库存或锁定库存) 大于 0
        LambdaQueryWrapper<WmsInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WmsInventory::getLocationId, id)
                .and(w -> w.gt(WmsInventory::getTotalQty, 0).or().gt(WmsInventory::getLockedQty, 0));

        Long count = wmsInventoryMapper.selectCount(wrapper);

        // 2. 如果 count > 0, 说明货位非空，禁止删除
        if (count > 0) {
            return false;
        }
        LambdaQueryWrapper<IotSensorData> sensorWrapper = new LambdaQueryWrapper<>();
        sensorWrapper.eq(IotSensorData::getLocationId, id);
        iotSensorDataMapper.delete(sensorWrapper);

        // 3. 货位为空，执行删除
        return removeById(id);
    }

    @Override
    @Transactional
    public boolean addLocationAndInitSensorData(BaseLocation entity) {
        // 1. 先调用 Mybatis-Plus 的 save 方法保存 BaseLocation
        boolean success = super.save(entity);

        // 2. 如果保存成功，自动插入初始传感器数据
        if (success) {
            Random random = new Random(); // 实例化 Random 对象

            // --- 生成随机数逻辑 ---

            // 1. 温度: 18.00°C 到 30.00°C 之间
            double tempMin = 18.0;
            double tempMax = 30.0;
            double randomTemp = tempMin + (tempMax - tempMin) * random.nextDouble();

            // 2. 湿度: 40.00% 到 70.00% 之间
            double humMin = 40.0;
            double humMax = 70.0;
            double randomHum = humMin + (humMax - humMin) * random.nextDouble();

            // 3. 报警状态: 90% 概率正常 (0), 10% 概率报警 (1)
            int alarmStatus = random.nextDouble() < 0.000000000001 ? 1 : 0; // 0.1 是 10%

            // --- 创建 IotSensorData 记录 ---

            // 获取新生成的货位ID
            Long newLocationId = entity.getId();

            IotSensorData initialSensorData = new IotSensorData();

            initialSensorData.setLocationId(newLocationId);

            // 设置随机温湿度数据，并格式化为两位小数的 BigDecimal
            // 使用 String.format("%.2f", ...) 来保证精度
            initialSensorData.setTemperature(new BigDecimal(String.format("%.2f", randomTemp)));
            initialSensorData.setHumidity(new BigDecimal(String.format("%.2f", randomHum)));
            initialSensorData.setIsAlarm(alarmStatus);

            // 插入到 iot_sensor_data 表
            iotSensorDataMapper.insert(initialSensorData);
        }

        return success;
    }
}