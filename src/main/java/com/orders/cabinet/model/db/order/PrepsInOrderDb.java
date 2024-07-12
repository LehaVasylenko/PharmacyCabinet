package com.orders.cabinet.model.db.order;

import com.orders.cabinet.event.EntityAuditListener;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    State state;

    @Column(name = "morion_id")
    String morionId;

    @Column(name = "ext_id")
    String extId;

    @Column(name = "price")
    Double price;

    @Column(name = "quantity")
    Double quant;

    @Column(name = "drug_name")
    String drugName;
}
