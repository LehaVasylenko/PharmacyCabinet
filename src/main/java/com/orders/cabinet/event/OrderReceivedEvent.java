package com.orders.cabinet.event;

import com.orders.cabinet.model.api.Order;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderReceivedEvent extends ApplicationEvent {
    private final Order[] order;

    public OrderReceivedEvent(Object source, Order[] order) {
        super(source);
        this.order = order;
    }
}

