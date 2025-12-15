package com.warehouse.warehouse_backend.dto.request;

import lombok.Data;

@Data
public class TaskAssignDTO {
    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 选中的机器人ID
     */
    private Long robotId;
}