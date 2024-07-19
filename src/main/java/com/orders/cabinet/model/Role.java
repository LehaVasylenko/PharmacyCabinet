package com.orders.cabinet.model;
/**
 * Enumeration representing the different roles in the system.
 *
 * <p>This enum defines the various roles that a user or entity can have within the system.
 * It is used for authorization and access control purposes to differentiate between
 * different types of users and their permissions.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
public enum Role {

    /**
     * Role for a shop user.
     *
     * <p>This role is assigned to users who have access to shop-specific functionalities
     * and are responsible for managing shop-related operations.</p>
     */
    SHOP,

    /**
     * Role for an administrative user.
     *
     * <p>This role is assigned to users with administrative privileges who have access
     * to system-wide functionalities and management tools.</p>
     */
    ADMIN;
}
