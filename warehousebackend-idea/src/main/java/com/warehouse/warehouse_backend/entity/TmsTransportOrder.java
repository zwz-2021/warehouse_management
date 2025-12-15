package com.warehouse.warehouse_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 物流运单表
 */
@Data
@TableName("tms_transport_order")
public class TmsTransportOrder {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String trackingNo;

    private String orderNo;

    private String logisticsCompany;

    /**
     * 状态: TRANSIT, DELIVERED
     */
    private String status;

    private LocalDateTime createTime;
}