package com.orders.cabinet.model.db;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "log_controllers")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ControllerEntityEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "controller_event")
    String controllerEvent;
}
