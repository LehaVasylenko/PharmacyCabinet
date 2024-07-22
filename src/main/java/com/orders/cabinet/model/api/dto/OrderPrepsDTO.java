package com.orders.cabinet.model.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;
/**
 * Data Transfer Object (DTO) for a list of drugs in an order.
 *
 * <p>This DTO is used to transfer information about individual drugs included in an order,
 * including the drug identifier, name, quantity, price, and confirmation flag.</p>
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
@Schema(description = "Data Transfer Object for List of drugs in order")
public class OrderPrepsDTO {

    /**
     * Drug identifier in the Morion database.
     *
     * <p>This field represents the unique identifier for the drug in the Morion database.</p>
     *
     * @example "7167"
     */
    @Schema(description = "Drug identifier in the Morion database", example = "7167")
    String morionId;

    /**
     * Drug name.
     *
     * <p>This field provides the name of the drug as it appears on the packaging or in the catalog.</p>
     *
     * @example "Никоретте® Зимняя мята 2 мг резинка жевательная лечебная блистер , №30, McNeil"
     */
    @Schema(description = "Drug name", example = "Никоретте® Зимняя мята 2 мг резинка жевательная лечебная блистер , №30, McNeil")
    String drugName;

    /**
     * Drug name.
     *
     * <p>This field provides the link to the drug at Compendium.</p>
     *
     * @example "https://compendium.com.ua/info/4556/7167/"
     */
    @Schema(description = "Drug link", example = "https://compendium.com.ua/info/4556/7167/")
    String drugLink;

    /**
     * Quantity of drug in the order.
     *
     * <p>This field specifies the quantity of the drug ordered. Must be zero or positive.</p>
     *
     * @example "1.0"
     */
    @NotNull
    @PositiveOrZero
    @Schema(description = "Quantity of drug in order", example = "1.0")
    Double quant;

    /**
     * Price of drug in the order.
     *
     * <p>This field indicates the price of the drug per unit in the order. Must be zero or positive.</p>
     *
     * @example "275.01"
     */
    @NotNull
    @PositiveOrZero
    @Schema(description = "Price of drug in order", example = "275.01")
    Double price;

    /**
     * Flag used when confirming an order for the first time.
     *
     * <p>This boolean flag indicates whether the drug was confirmed as part of the order for the first time.
     * This flag is not used in further actions.</p>
     *
     * @example "true"
     */
    @Schema(description = "Flag used when confirming an order for the first time. Not used in further actions", example = "true")
    boolean confirmed;
}
