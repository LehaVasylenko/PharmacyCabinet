package com.orders.cabinet.model.db;

import com.orders.cabinet.model.Role;
import com.orders.cabinet.model.db.order.OrderDb;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "shops")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Shops {
    @Id
    @Column(name = "shop_id", unique = true)
    String shopId;

    @Column(name = "password")
    String password;

    @Enumerated(EnumType.STRING)
    Role role;

    @ManyToOne
    @JoinColumn(name = "corp_id", nullable = false)
    Corp corp;

    @OneToMany(mappedBy = "shop")
    List<OrderDb> orders;
}
