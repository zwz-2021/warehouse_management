package com.warehouse.warehouse_backend.event;

import com.warehouse.warehouse_backend.entity.WmsOutboundNote;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 事件：出库单已生成（等待拣选）
 */
@Getter
public class OutboundPendingEvent extends ApplicationEvent {

    private final WmsOutboundNote outboundNote;

    public OutboundPendingEvent(Object source, WmsOutboundNote outboundNote) {
        super(source);
        this.outboundNote = outboundNote;
    }
}