package com.warehouse.warehouse_backend.dto.request;

import lombok.Data;

@Data
public class InboundCreateDTO {

    private Long goodsId;

    private Integer actualQty;

    private Integer qcStatus;

    private Integer assignMethod;
}