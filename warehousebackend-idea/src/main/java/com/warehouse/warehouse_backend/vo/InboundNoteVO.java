package com.warehouse.warehouse_backend.vo;

import com.warehouse.warehouse_backend.entity.WmsInboundNote;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 入库单显示视图 (增加名称和编码字段)
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InboundNoteVO extends WmsInboundNote {

    // 从 base_goods 表查出来的
    private String goodsName;

    // 从 base_location 表查出来的
    private String locationCode;
}