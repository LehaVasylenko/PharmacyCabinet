package com.orders.cabinet.model.db;

import com.orders.cabinet.event.EntityAuditListener;
import com.orders.cabinet.model.Role;
import com.orders.cabinet.model.db.order.OrderDb;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "shops")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString(exclude = {"corp", "orders"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(EntityAuditListener.class)
public class Shops {
    @Id
    @Column(name = "shop_id", unique = true)
    @NotEmpty
    String shopId;

    @Column(name = "password")
    @NotEmpty
    String password;

    @Enumerated(EnumType.STRING)
    Role role;

    @Column(name = "logged")
    boolean logged;

    @ManyToOne
    @JoinColumn(name = "corp_id", nullable = false)
    Corp corp;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderDb> orders;
}
