package com.orders.cabinet.model.db;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "corp")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Corp {

    @Id
    @Column(name = "corp_id", unique = true)
    String corpId;

    @Column(name = "login")
    String login;

    @Column(name = "password")
    String password;

    @Column(name = "corp_name")
    String corpName;

    @Column(name = "life_time")
    Integer lifeTime;

    @OneToMany(mappedBy = "corp")
    List<Shops> shops;
}
