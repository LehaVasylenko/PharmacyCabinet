package com.orders.cabinet.model.db.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Base entity class providing a common identifier field for all entities.
 *
 * <p>This class serves as a base class for other entities, providing a common {@code id}
 * field that is automatically generated and used as the primary key. It is intended
 * to be extended by other entity classes that require a unique identifier.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {

    /**
     * The unique identifier for the entity.
     *
     * <p>This field is the primary key for the entity and is automatically generated
     * by the persistence provider using the {@code GenerationType.IDENTITY} strategy.
     * It is marked as {@code updatable = false} to ensure it is not modified after
     * entity creation.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    Long id;
}
