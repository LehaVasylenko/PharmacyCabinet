package com.orders.cabinet.model.db.order;

import com.orders.cabinet.model.db.Shops;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.builder.ToStringExclude;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDb extends BaseEntity{

    @Column(name = "order_id")
    String orderId;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    Shops shop;

    @Column(name = "shop_ext_id")
    String shopExtId;

    @Column(name = "phone")
    String phone;

    @Column(name = "agent")
    String agent;

    @Column(name = "time")
    Long timestamp;

    @Column(name = "shipping")
    String shipping;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    List<State> states;
}
