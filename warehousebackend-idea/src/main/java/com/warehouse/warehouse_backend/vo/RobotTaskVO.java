package com.warehouse.warehouse_backend.vo;

import com.warehouse.warehouse_backend.entity.WmsRobotTask;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 机器人任务详情视图
 * 继承 WmsRobotTask，自动拥有 id, taskNo, status 等所有字段
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RobotTaskVO extends WmsRobotTask {

    // 显示机器人名字
    private String robotName;

    // 显示货物名字 (需要根据单据ID去查)
    private String goodsName;
}