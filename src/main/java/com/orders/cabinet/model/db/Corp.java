package com.orders.cabinet.model.db;

import com.orders.cabinet.event.EntityAuditListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

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

    @Id
    @Column(name = "corp_id", unique = true)
    @NotEmpty
    String corpId;

    @Column(name = "login")
    @NotEmpty
    String login;

    @Column(name = "password")
    @NotEmpty
    String password;

    @Column(name = "corp_name")
    @NotEmpty
    String corpName;

    @Column(name = "life_time")
    @Min(24)
    Integer lifeTime;

    @OneToMany(mappedBy = "corp", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Shops> shops;
}
