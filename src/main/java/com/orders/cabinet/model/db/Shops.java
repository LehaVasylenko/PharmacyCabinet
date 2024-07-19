package com.orders.cabinet.model.db;

import com.orders.cabinet.event.EntityAuditListener;
import com.orders.cabinet.model.Role;
import com.orders.cabinet.model.db.order.OrderDb;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
/**
 * Represents a shop entity in the system.
 *
 * <p>This entity class maps to the "shops" table in the database and includes information
 * about a shop, such as its ID, password, role, and associated corporation. It also maintains
 * a list of orders associated with the shop.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
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

    /**
     * The unique identifier for the shop.
     *
     * <p>This field serves as the primary key for the "shops" table.</p>
     */
    @Id
    @Column(name = "shop_id", unique = true)
    @NotEmpty
    String shopId;

    /**
     * The password associated with the shop.
     *
     * <p>This field stores the password for authentication purposes.</p>
     */
    @Column(name = "password")
    @NotEmpty
    String password;

    /**
     * The role assigned to the shop.
     *
     * <p>This field indicates the role of the shop, such as "SHOP".</p>
     */
    @Enumerated(EnumType.STRING)
    Role role;

    /**
     * Indicates whether the shop is logged in.
     *
     * <p>This field is used to track the login status of the shop.</p>
     */
    @Column(name = "logged")
    boolean logged;

    /**
     * The corporation to which the shop belongs.
     *
     * <p>This field establishes a many-to-one relationship with the {@link Corp} entity,
     * indicating which corporation owns the shop.</p>
     */
    @ManyToOne
    @JoinColumn(name = "corp_id", nullable = false)
    Corp corp;

    /**
     * The list of orders associated with the shop.
     *
     * <p>This field maintains a one-to-many relationship with the {@link OrderDb} entity,
     * storing all orders placed at the shop.</p>
     */
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderDb> orders;
}
