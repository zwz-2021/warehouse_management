package com.warehouse.warehouse_backend.vo;

import com.warehouse.warehouse_backend.entity.WmsInventory;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库存查询视图对象 (VO)
 * 继承 WmsInventory 的字段，并添加联表查询所需的名称和编码。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InventoryDetailVO extends WmsInventory {

    // 联表查询得到的货物名称
    private String goodsName;

    // 联表查询得到的货位编码
    private String locationCode;
}