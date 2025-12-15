package com.warehouse.warehouse_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 货物基础信息表
 */
@Data
@TableName("base_goods")
public class BaseGoods {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String goodsCode;

    private String goodsName;

    private String spec;

    private String unit;

    private BigDecimal price;

    /**
     * 库存预警阈值
     */
    private Integer warnThreshold;
}