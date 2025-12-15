package com.warehouse.warehouse_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 出库单明细
 */
@Data
@TableName("wms_outbound_note")
public class WmsOutboundNote {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long goodsId;

    private Integer qty;

    /**
     * 拣选状态: 0-待拣选, 1-已拣选
     */
    private Integer pickStatus;
}