package com.orders.cabinet.model.db.order;

import com.orders.cabinet.model.db.Shops;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "states")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class State extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    OrderDb order;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    Shops shop;

    @Column(name = "time")
    Date time;

    @Column(name = "state")
    String state;

    @Column(name = "cancel_reason")
    String reason;

    @OneToMany(mappedBy = "state", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    List<PrepsInOrderDb> prepsInOrder;
}
