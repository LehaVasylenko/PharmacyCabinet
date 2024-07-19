package com.orders.cabinet.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
/**
 * Represents a preparation (item) within an order.
 *
 * <p>This class contains details about individual items in an order, including identifiers,
 * quantity, and price.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
 public class OrderPreps {

   /**
    * Identifier of the preparation (item).
    *
    * <p>This field represents the unique identifier for the preparation in the order.</p>
    */
    String id;

   /**
    * External identifier of the preparation (item).
    *
    * <p>This field represents an external unique identifier for the preparation, if available.</p>
    */
    @JsonProperty("ext_id")
    String extId;

   /**
    * Quantity of the preparation (item) in the order.
    *
    * <p>This field specifies the amount of the preparation included in the order.</p>
    */
    Double quant;

    /**
    * Price of the preparation (item).
    *
    * <p>This field indicates the cost of the preparation in the order.</p>
    */
    Double price;
}
