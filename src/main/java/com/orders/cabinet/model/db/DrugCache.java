package com.orders.cabinet.model.db;

import com.orders.cabinet.event.EntityAuditListener;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
/**
 * Represents a cached drug entry in the system.
 *
 * <p>This entity class maps to the "drug_cache" table in the database and stores information
 * about drugs, including their unique identifier and name.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Entity
@Table(name = "drug_cache")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(EntityAuditListener.class)
public class DrugCache {

    /**
     * The unique identifier for the drug.
     *
     * <p>This field is used as the primary key for the "drug_cache" table.</p>
     * <p>This field cannot be null.</p>
     */
    @Id
    @Column(name = "drug_id", unique = true, nullable = false)
    String drugId;

    /**
     * The name of the drug.
     *
     * <p>This field stores the name of the drug.</p>
     * <p>This field cannot be null.</p>
     */
    @Column(name = "drug_name", nullable = false)
    String drugName;

    /**
     * A link to the drug info.
     *
     * <p>This field stores a link to the drug.</p>
     * <p>This field cannot be null.</p>
     */
    @Column(name = "drug_link", nullable = false)
    String drugLink;
}
