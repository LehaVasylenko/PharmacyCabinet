package com.orders.cabinet.model.db.order;

import com.orders.cabinet.event.EntityAuditListener;
import com.orders.cabinet.model.db.Shops;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;
/**
 * Entity representing the state of an order in the system.
 *
 * <p>This class maps to the {@code states} table in the database and represents the
 * different states an order can go through during its lifecycle. Each state is associated
 * with an order and a shop, and includes a timestamp, state description, and optional
 * cancellation reason. The state also maintains a list of drugs associated with it.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "states")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString(exclude = {"shop", "order", "prepsInOrder"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(EntityAuditListener.class)
public class State extends BaseEntity{

    /**
     * The order associated with this state.
     *
     * <p>This field establishes a many-to-one relationship with the {@code orders} table,
     * indicating that each state is associated with a single order. It is marked as
     * {@code nullable = false} to ensure that every state is linked to an order.</p>
     */
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    OrderDb order;

    /**
     * The shop associated with this state.
     *
     * <p>This field establishes a many-to-one relationship with the {@code shops} table,
     * indicating that each state is associated with a single shop. It is marked as
     * {@code nullable = false} to ensure that every state is linked to a shop.</p>
     */
    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    Shops shop;

    /**
     * The timestamp when the state was recorded.
     *
     * <p>This field represents the time when the state was recorded, stored as a {@code Date} object.</p>
     */
    @Column(name = "time")
    Date time;

    /**
     * The description of the state.
     *
     * <p>This field stores the description of the state, such as "New", "Confirmed", "Canceled", etc.</p>
     */
    @Column(name = "state")
    String state;

    /**
     * The reason for canceling the order, if applicable.
     *
     * <p>This field stores the reason for canceling the order, used only if the state is "Canceled".</p>
     */
    @Column(name = "cancel_reason")
    String reason;

    /**
     * List of drugs associated with this state.
     *
     * <p>This field establishes a one-to-many relationship with the {@code preps_in_order} table,
     * indicating that a state can have multiple drugs associated with it. The list is fetched eagerly
     * and cascaded, meaning changes to the state’s drugs are persisted automatically.</p>
     */
    @OneToMany(mappedBy = "state", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    List<PrepsInOrderDb> prepsInOrder;
}
