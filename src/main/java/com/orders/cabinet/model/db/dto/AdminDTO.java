package com.orders.cabinet.model.db.dto;

import com.orders.cabinet.model.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminDTO {
    String username;
    String password;
    Role role;
}
