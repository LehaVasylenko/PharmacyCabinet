package com.orders.cabinet.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
/**
 * Represents an order with various attributes and details.
 *
 * <p>This class contains information related to an order, including shop and order identifiers,
 * customer phone number, timestamp, state, and a list of ordered items.</p>
 * <p>This is response of 'https://booking.geoapteka.com.ua/pop-order'</p>
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
public class Order {

   /**
    * Identifier of the shop where the order was placed.
    *
    * <p>This field represents the unique identifier of the shop associated with the order.</p>
    */
   @JsonProperty("id_shop")
   String idShop;

   /**
    * External identifier of the shop where the order was placed.
    *
    * <p>This field represents an external unique identifier for the shop, if available.</p>
    */
   @JsonProperty("ext_id_shop")
   String extidShop;

   /**
    * Phone number of the customer who placed the order.
    *
    * <p>This field contains the contact phone number of the customer.</p>
    */
   String phone;

   /**
    * Flag indicating whether the order is a test order.
    *
    * <p>This field indicates if the order is a test or real order. {@code true} if test, {@code false} otherwise.</p>
    */
   Boolean test;

   /**
    * Message flags associated with the order. I don't give a fuck what is this shit about. Nobody knows
    *
    * <p>This field represents any flags related to the messaging system used for the order.</p>
    */
   @JsonProperty("MsgFlags")
   Integer msgFlags;

   /**
    * Unix-time when the order was created.
    *
    * <p>This field represents the time at which the order was created or received, in epoch seconds.</p>
    */
   Long timestamp;

   /**
    * Identifier of the agent who handled or created the order. Apteki, Compendium or Apps
    *
    * <p>This field contains the identifier of the agent associated with the order.</p>
    */
   String agent;

   /**
    * Unique identifier of the order.
    *
    * <p>This field represents the unique identifier assigned to the order.</p>
    */
   @JsonProperty("id_order")
   String idOrder;

   /**
    * Shipping information for the order. Optima or pickup
    *
    * <p>This field contains details about the shipping method or instructions for the order.</p>
    */
   String shipping;

   /**
    * Current state of the order. Usually 'New'
    *
    * <p>This field describes the current status of the order, such as 'New', 'Confirmed', 'Shipped', etc.</p>
    */
   String state;

   /**
    * Reason for the cancel of the order.
    *
    * <p>This field provides the reason or explanation for the current state of the order, if applicable.</p>
    */
   String reason;

   /**
    * Additional attributes associated with the order. The same shit as MsgFlags
    *
    * <p>This field contains any additional attributes or metadata related to the order.</p>
    */
   String attribute;

   /**
    * List of items (preparations) included in the order.
    *
    * <p>This field contains a list of {@link OrderPreps} representing the drugs or items included in the order.</p>
    */
   List<OrderPreps> data;
}

