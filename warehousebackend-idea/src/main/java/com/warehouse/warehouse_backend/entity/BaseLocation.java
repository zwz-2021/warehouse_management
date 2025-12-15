package com.warehouse.warehouse_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 仓库货位表
 */
@Data
@TableName("base_location")
public class BaseLocation {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 货位编号 (如 A-01-01)
     */
    private String locationCode;

    private String areaZone;

    /**
     * 状态: 0-空闲, 1-占用
     */
    private Integer status;

    private Double maxTemperature;

    private Double maxHumidity;
}