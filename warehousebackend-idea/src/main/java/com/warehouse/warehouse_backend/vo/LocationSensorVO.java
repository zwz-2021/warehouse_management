package com.warehouse.warehouse_backend.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 货位 + 传感器数据的混合视图
 * 前端拿到这个对象，既能显示货位号，又能显示温度
 */
@Data
public class LocationSensorVO {
    // === 货位信息 (来自 base_location) ===
    private Long id;            // 货位ID
    private String locationCode; // 货位编号 (A-01-01)
    private String areaZone;    // 区域
    private Integer status;     // 状态 (空闲/占用)

    // === 传感器信息 (来自 iot_sensor_data) ===
    // 注意：如果是历史记录表，这里通常显示“最新一次”的数据
    private BigDecimal temperature; // 温度
    private BigDecimal humidity;    // 湿度
    private Integer isAlarm;        // 是否报警
}