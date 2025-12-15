package com.warehouse.warehouse_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * IoT传感器数据记录
 */
@Data
@TableName("iot_sensor_data")
public class IotSensorData {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long locationId;

    private BigDecimal temperature;

    private BigDecimal humidity;

    /**
     * 是否报警: 0-正常, 1-异常
     */
    private Integer isAlarm;

    private LocalDateTime recordTime;
}