package com.warehouse.warehouse_backend.event;

import com.warehouse.warehouse_backend.entity.WmsRobotTask;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 事件：机器人任务已完成
 */
@Getter
public class TaskCompletedEvent extends ApplicationEvent {

    private final WmsRobotTask task;

    public TaskCompletedEvent(Object source, WmsRobotTask task) {
        super(source);
        this.task = task;
    }
}