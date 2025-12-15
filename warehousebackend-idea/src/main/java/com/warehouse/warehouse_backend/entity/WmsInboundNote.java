package com.warehouse.warehouse_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 入库单明细表
 */
@Data
@TableName("wms_inbound_note")
public class WmsInboundNote {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String poNo;

    private Long goodsId;

    private Integer planQty;

    private Integer actualQty;

    /**
     * 质检状态: 0-待检, 1-合格, 2-不合格
     */
    private Integer qcStatus;

    /**
     * 上架状态: 0-待分配, 1-待上架, 2-已上架
     */
    private Integer putawayStatus;

    private Long targetLocationId;

    private Integer assignMethod;
}