package com.orders.cabinet.model.db;

import com.orders.cabinet.model.Role;
import com.orders.cabinet.model.db.order.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Admin extends BaseEntity {
    String username;
    String password;
    @Enumerated(EnumType.STRING)
    Role role;
}
