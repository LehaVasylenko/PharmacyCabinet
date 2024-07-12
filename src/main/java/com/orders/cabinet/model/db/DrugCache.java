package com.orders.cabinet.model.db;

import com.orders.cabinet.event.EntityAuditListener;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "drug_cache")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(EntityAuditListener.class)
public class DrugCache {
    @Id
    @Column(name = "drug_id", unique = true, nullable = false)
    String drugId;

    @Column(name = "drug_name", nullable = false)
    String drugName;
}
