package com.orders.cabinet.model.db.order;

import com.orders.cabinet.event.EntityAuditListener;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
/**
 * Entity representing drugs associated with an order state.
 *
 * <p>This class maps to the {@code preps_in_order} table in the database and represents
 * the drugs associated with a particular state of an order. It includes fields for
 * drug identifiers, price, quantity, and name, and maintains a relationship with the
 * {@code State} entity that this drug is associated with.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "preps_in_order")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString(exclude = "state")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(EntityAuditListener.class)
public class PrepsInOrderDb extends BaseEntity{

    /**
     * The state associated with this drug.
     *
     * <p>This field establishes a many-to-one relationship with the {@code states} table,
     * indicating that each drug is associated with a single state of an order. It is marked
     * as {@code nullable = false} to ensure that every drug is linked to a state.</p>
     */
    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    State state;

    /**
     * The Morion identifier of the drug.
     *
     * <p>This field stores the unique identifier of the drug in the Morion database.</p>
     */
    @Column(name = "morion_id")
    String morionId;

    /**
     * The external identifier of the drug.
     *
     * <p>This field stores the external identifier associated with the drug.</p>
     */
    @Column(name = "ext_id")
    String extId;

    /**
     * The price of the drug.
     *
     * <p>This field stores the price of the drug as a {@code Double}.</p>
     */
    @Column(name = "price")
    Double price;

    /**
     * The quantity of the drug.
     *
     * <p>This field stores the quantity of the drug ordered, represented as a {@code Double}.</p>
     */
    @Column(name = "quantity")
    Double quant;

    /**
     * The name of the drug.
     *
     * <p>This field stores the name of the drug.</p>
     */
    @Column(name = "drug_name")
    String drugName;

    /**
     * The link to the drug info
     *
     * <p>This field stores the link to the drug info at Compendium.</p>
     */
    @Column(name = "drug_link")
    String drugLink;
}
