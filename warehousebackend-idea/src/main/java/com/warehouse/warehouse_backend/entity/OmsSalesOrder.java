package com.warehouse.warehouse_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 销售订单表
 */
@Data
@TableName("oms_sales_order")
public class OmsSalesOrder {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long customerId;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    /**
     * 状态: 0-已创建, 1-已锁库, 2-拣选中, 3-已发货, 4-已完成
     */
    private Integer status;

    private LocalDateTime createTime;

    private Long goodsId;
}