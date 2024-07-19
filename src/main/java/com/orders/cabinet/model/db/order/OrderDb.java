package com.orders.cabinet.model.db.order;

import com.orders.cabinet.event.EntityAuditListener;
import com.orders.cabinet.model.db.Shops;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
/**
 * Entity representing an order in the system.
 *
 * <p>This class maps to the {@code orders} table in the database and represents
 * an order made by a shop. It contains information about the order, such as its
 * ID, associated shop, phone number, agent, timestamp, shipping details, and states.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString(exclude = {"shop", "states"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(EntityAuditListener.class)
public class OrderDb extends BaseEntity{

    /**
     * Unique identifier for the order.
     *
     * <p>This field represents the unique ID of the order as stored in the {@code orders}
     * table.</p>
     */
    @Column(name = "order_id")
    String orderId;

    /**
     * The shop associated with this order.
     *
     * <p>This field establishes a many-to-one relationship with the {@code shops} table,
     * indicating that each order is associated with a single shop. It is marked as
     * {@code nullable = false} to ensure that every order is linked to a shop.</p>
     */
    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    Shops shop;

    /**
     * External ID of the shop associated with this order.
     *
     * <p>This field represents the external identifier of the shop associated with the
     * order.</p>
     */
    @Column(name = "shop_ext_id")
    String shopExtId;

    /**
     * Phone number of User.
     *
     * <p>This field stores the phone number of the user who placed the order.</p>
     */
    @Column(name = "phone")
    String phone;

    /**
     * Source where order was made: Compendium, Apteki, or App.
     *
     * <p>This field stores the identifier or name of the agent handling the order.</p>
     */
    @Column(name = "agent")
    String agent;

    /**
     * Unix-time when the order was created.
     *
     * <p>This field represents the time when the order was placed, stored as a Unix timestamp.</p>
     */
    @Column(name = "time")
    Long timestamp;

    /**
     * Shipping details for the order. Optima or pickup
     *
     * <p>This field contains information about the shipping details for the order.</p>
     */
    @Column(name = "shipping")
    String shipping;

    /**
     * List of states associated with the order.
     *
     * <p>This field establishes a one-to-many relationship with the {@code states} table,
     * indicating that an order can have multiple states throughout its lifecycle. The
     * list is fetched eagerly and cascaded, meaning changes to the order's states are
     * persisted automatically.</p>
     */
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    List<State> states;
}
