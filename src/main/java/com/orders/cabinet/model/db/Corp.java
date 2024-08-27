package com.orders.cabinet.model.db;

import com.orders.cabinet.event.EntityAuditListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
/**
 * Represents a corporation in the system.
 *
 * <p>This entity class maps to the "corp" table in the database and stores information
 * about corporations, including their credentials, name, and associated shops.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Entity
@Table(name = "corp")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString(exclude = "shops")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(EntityAuditListener.class)
public class Corp {

    /**
     * The unique identifier for the corporation.
     *
     * <p>This field is used as the primary key for the "corp" table.</p>
     */
    @Id
    @Column(name = "corp_id", unique = true)
    @NotEmpty
    String corpId;

    /**
     * The login name for the corporation.
     *
     * <p>This field represents the login credentials for accessing Booking and Sky-Net.</p>
     */
    @Column(name = "login")
    @NotEmpty
    String login;

    /**
     * The password for the corporation's login.
     *
     * <p>This field stores the password associated with the corporation's login credentials.</p>
     */
    @Column(name = "password")
    @NotEmpty
    String password;

    /**
     * The name of the corporation.
     *
     * <p>This field stores the name of the corporation.</p>
     */
    @Column(name = "corp_name")
    @NotEmpty
    String corpName;

    /**
     * The lifetime of the corporation's account in hours.
     *
     * <p>This field indicates how long the corporation's account is valid, measured in hours.</p>
     * <p>It must be at least 24 hours.</p>
     */
    @Column(name = "life_time")
    @Min(24)
    Integer lifeTime;

    /**
     * The list of shops associated with this corporation.
     *
     * <p>This field represents the one-to-many relationship between the corporation and its shops.</p>
     * <p>The cascade type is set to {@link CascadeType#ALL}, and orphan removal is enabled.</p>
     */
    @OneToMany(mappedBy = "corp", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Shops> shops;
}
