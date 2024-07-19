package com.orders.cabinet.model.db;

import com.orders.cabinet.event.EntityAuditListener;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
/**
 * Represents a cached information of a shop.
 *
 * <p>This entity class maps to the "shop_info_cache" table in the database and stores information
 * about a shop, including its ID, corporation details, and location information.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Entity
@Table(name = "shop_info_cache")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(EntityAuditListener.class)
public class ShopInfoCache {

    /**
     * The unique identifier for the shop.
     *
     * <p>This field is used as the primary key for the "shop_info_cache" table.</p>
     */
    @Id
    @Column(name = "shop_id", unique = true)
    String shopId;

    /**
     * The unique identifier for the corporation to which the shop belongs.
     *
     * <p>This field references the corporation ID associated with the shop.</p>
     */
    @Column(name = "corp_id")
    String corpId;

    /**
     * The name of the corporation to which the shop belongs.
     *
     * <p>This field stores the name of the corporation associated with the shop.</p>
     */
    @Column(name = "corp_name")
    String corpName;

    /**
     * The name of the shop.
     *
     * <p>This field stores the name of the shop.</p>
     */
    @Column(name = "shop_name")
    String name;

    /**
     * The mark or label associated with the shop.
     *
     * <p>This field stores any mark or label assigned to the shop.</p>
     */
    @Column(name = "mark")
    String mark;

    /**
     * The area where the shop is located.
     *
     * <p>This field stores the area of the shop's location.</p>
     */
    @Column(name = "area")
    String area;

    /**
     * The city where the shop is located.
     *
     * <p>This field stores the city of the shop's location.</p>
     */
    @Column(name = "city")
    String city;

    /**
     * The street where the shop is located.
     *
     * <p>This field stores the street address of the shop.</p>
     */
    @Column(name = "street")
    String street;

    /**
     * The last update timestamp or version information for the shop's data. Deprecated
     *
     * <p>This field stores information about the last update to the shop's data.</p>
     */
    @Column(name = "update")
    String update;

    /**
     * The shop's open hours.
     *
     * <p>This field stores the opening hours of the shop.</p>
     */
    @Column(name = "open_hours")
    String openHours;

}
