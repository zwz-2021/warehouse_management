package com.warehouse.warehouse_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实时库存表
 */
@Data
@TableName("wms_inventory")
public class WmsInventory {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long goodsId;

    private Long locationId;

    /**
     * 物理库存
     */
    private Integer totalQty;

    /**
     * 锁定库存(预占)
     */
    private Integer lockedQty;

    private LocalDateTime updateTime;
}