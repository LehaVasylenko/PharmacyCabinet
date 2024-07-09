package com.orders.cabinet.model.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "drug_cache")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DrugCache {
    @Id
    @Column(name = "drug_id", unique = true, nullable = false)
    String drugId;

    @Column(name = "drug_name", nullable = false)
    String drugName;
}
