package com.warehouse.warehouse_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 机器人任务调度表
 */
@Data
@TableName("wms_robot_task")
public class WmsRobotTask {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskNo;

    /**
     * 类型: INBOUND, OUTBOUND
     */
    private String taskType;

    private Long robotId;

    /**
     * 关联单据ID
     */
    private Long sourceDocId;

    private Long targetLocationId;

    /**
     * 状态: PENDING, ASSIGNED, IN_PROGRESS, COMPLETED
     */
    private String status;

    private LocalDateTime createTime;
}