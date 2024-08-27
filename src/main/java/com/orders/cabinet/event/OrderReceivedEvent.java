package com.orders.cabinet.event;

import com.orders.cabinet.model.api.Order;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
/**
 * Event published when an order is received.
 *
 * <p>This event is used to encapsulate the details of an order or a list of orders that have been received.
 * It extends {@link ApplicationEvent} to integrate with the Spring event system.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Getter
public class OrderReceivedEvent extends ApplicationEvent {
    private final Order[] order;

    /**
     * Constructs a new {@code OrderReceivedEvent}.
     *
     * @param source the object on which the event initially occurred (cannot be {@code null})
     * @param order an array of {@link Order} objects that were received
     */
    public OrderReceivedEvent(Object source, Order[] order) {
        super(source);
        this.order = order;
    }
}

