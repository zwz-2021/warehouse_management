package com.warehouse.warehouse_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * AGV机器人表
 */
@Data
@TableName("base_robot")
public class BaseRobot {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String robotCode;

    private String robotName;

    /**
     * 状态: IDLE, BUSY, ERROR, CHARGING
     */
    private String status;

    /**
     * 调度模式: AUTO, MANUAL
     */
    private String workMode;

    private Integer battery;
}