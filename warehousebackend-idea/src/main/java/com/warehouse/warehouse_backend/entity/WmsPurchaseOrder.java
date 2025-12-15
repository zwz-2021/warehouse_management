package com.warehouse.warehouse_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 采购单主表
 */
@Data
@TableName("wms_purchase_order")
public class WmsPurchaseOrder {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String poNo;

    private String supplierName;

    /**
     * 状态: 0-待入库, 1-入库中, 2-已完成
     */
    private Integer status;

    private LocalDateTime createTime;
}