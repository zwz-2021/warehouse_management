package com.warehouse.warehouse_backend.vo;

import com.warehouse.warehouse_backend.entity.WmsOutboundNote;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WmsOutboundNoteVO extends WmsOutboundNote {
    // 联表查出来的货物名称
    private String goodsName;
}