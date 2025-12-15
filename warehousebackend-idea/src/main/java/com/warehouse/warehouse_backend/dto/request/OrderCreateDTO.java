package com.warehouse.warehouse_backend.dto.request;

import lombok.Data;

@Data
public class OrderCreateDTO {
    // 客户ID (关联 sys_user 表的 id)
    private Long customerId;

    // ★ 修改：接收货物ID，而不是名称
    private Long goodsId;

    private Integer qty;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
}