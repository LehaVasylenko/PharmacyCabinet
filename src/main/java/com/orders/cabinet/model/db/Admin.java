package com.orders.cabinet.model.db;

import com.orders.cabinet.event.EntityAuditListener;
import com.orders.cabinet.model.Role;
import com.orders.cabinet.model.db.order.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
/**
 * Represents an administrative user in the system.
 *
 * <p>This entity class maps to the "admin" table in the database and stores information
 * about administrative users, including their credentials and roles.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(EntityAuditListener.class)
public class Admin extends BaseEntity {

    /**
     * The username of the admin.
     *
     * <p>This field represents the login name of the administrative user.</p>
     */
    String username;

    /**
     * The password of the admin.
     *
     * <p>This field stores the password for the administrative user's account.</p>
     */
    String password;

    /**
     * The role assigned to the admin. Is entered automatically by the system upon registration.
     *
     * <p>This field represents the role of the administrative user within the system.</p>
     * <p>Roles are defined by the {@link Role} enumeration.</p>
     */
    @Enumerated(EnumType.STRING)
    Role role;
}
