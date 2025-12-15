package com.warehouse.warehouse_backend.event;

import com.warehouse.warehouse_backend.entity.WmsInboundNote;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 事件：入库单已创建且合格（等待上架）
 */
@Getter
public class InboundPendingEvent extends ApplicationEvent {

    private final WmsInboundNote inboundNote;

    public InboundPendingEvent(Object source, WmsInboundNote inboundNote) {
        super(source);
        this.inboundNote = inboundNote;
    }
}