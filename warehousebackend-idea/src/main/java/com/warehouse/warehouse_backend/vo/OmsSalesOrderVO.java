package com.warehouse.warehouse_backend.vo;

import com.warehouse.warehouse_backend.entity.OmsSalesOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 销售订单视图对象
 * 用于前端展示（包含联表查询出来的名称）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OmsSalesOrderVO extends OmsSalesOrder {

    // 联表查出来的货物名称
    private String goodsName;

    // 联表查出来的客户名称 (如果有需要)
    private String customerName;
    private String realName; // 客户真实姓名
}