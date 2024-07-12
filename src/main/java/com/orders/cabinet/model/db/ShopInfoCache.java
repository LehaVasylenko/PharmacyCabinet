package com.orders.cabinet.model.db;

import com.orders.cabinet.event.EntityAuditListener;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "shop_info_cache")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(EntityAuditListener.class)
public class ShopInfoCache {
    @Id
    @Column(name = "shop_id", unique = true)
    String shopId;

    @Column(name = "corp_id")
    String corpId;

    @Column(name = "corp_name")
    String corpName;

    @Column(name = "shop_name")
    String name;

    @Column(name = "mark")
    String mark;

    @Column(name = "area")
    String area;

    @Column(name = "city")
    String city;

    @Column(name = "street")
    String street;

    @Column(name = "update")
    String update;

    @Column(name = "open_hours")
    String openHours;

}
