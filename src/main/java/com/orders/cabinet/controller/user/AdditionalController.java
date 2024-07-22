package com.orders.cabinet.controller.user;

import com.orders.cabinet.exception.NoSuchShopException;
import com.orders.cabinet.model.api.dto.OrderDTO;
import com.orders.cabinet.model.db.dto.ShopInfoCacheDTO;
import com.orders.cabinet.service.AdditionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
/**
 * Controller for additional user operations.
 *
 * <p>This controller provides endpoints for users to get all orders or find orders by the last 4 symbols.</p>
 *
 * @version 1.0
 * @since 2024-07-19
 *
 * @see OrderDTO
 * @see ShopInfoCacheDTO
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 */
@RestController
@RequestMapping("${user.additional}")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Additional User Controller", description = "Allows users to get all orders or find order by last 4 symbols")
public class AdditionalController {
    AdditionalService additionalService;

    /**
     * Find orders by the last 4 symbols.
     *
     * <p>Allowed for Shops. Returns a list of orders corresponding to the pharmacy which ends with the specified 4 symbols.</p>
     *
     * @param userDetails the authenticated user details
     * @param last the last 4 symbols of the order identifier
     * @return a response entity containing a list of order DTOs
     */
    @PostMapping("/get/last")
    @Operation(summary = "Find orders by last 4 symbols",
            description = "Allowed for Shops. Returns list of orders corresponding to the pharmacy which ends with specified 4 symbols",
            tags = {"Get"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "204", description = "No orders were found",
                    content = @Content(
                    schema = @Schema(implementation = ShopInfoCacheDTO.class),
                    examples = @ExampleObject(value = """
                    [
                                   {
                                     "phone": "Error message 1",
                                     "time": "Error message 2",
                                     "idOrder": "Error message 3",
                                   }
                                 ]
                """)
            )),
            @ApiResponse(responseCode = "400", description = "Bad request. Something wrong",
                    content = @Content(
                            schema = @Schema(implementation = ShopInfoCacheDTO.class),
                            examples = @ExampleObject(value = """
                    [
                                   {
                                     "phone": "Error message 1",
                                     "time": "Error message 2",
                                     "idOrder": "Error message 3",
                                   }
                                 ]
                """)
                    )),
            @ApiResponse(responseCode = "401", description = "Not authorized",
                    content = @Content(
                            examples = @ExampleObject(value = "     ")
                    )),
            @ApiResponse(responseCode = "500", description = "Some error. Error message should be in response body",
                    content = @Content(
                            schema = @Schema(implementation = ShopInfoCacheDTO.class),
                            examples = @ExampleObject(value = """
                    [
                                   {
                                     "phone": "Error message 1",
                                     "time": "Error message 2",
                                     "idOrder": "Error message 3",
                                   }
                                 ]
                """)
                    ))
    })
    public CompletableFuture<ResponseEntity<List<OrderDTO>>> findByLast4Symbol(@AuthenticationPrincipal UserDetails userDetails,
                                                                               @RequestBody String last) {
        return additionalService.getOrderBy4LastSymbols(userDetails.getUsername(), last)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause();
                    if (cause instanceof NoSuchShopException) {
                        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(List.of(OrderDTO
                                .builder()
                                    .idOrder(ex.getMessage())
                                    .phone(ex.getCause().getMessage())
                                    .time(ex.getLocalizedMessage())
                                .build()));
                    } else if (cause instanceof NoSuchElementException || cause instanceof IllegalArgumentException) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of(OrderDTO
                                .builder()
                                    .idOrder(ex.getMessage())
                                    .phone(ex.getCause().getMessage())
                                    .time(ex.getLocalizedMessage())
                                .build()));
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of(OrderDTO
                                .builder()
                                    .idOrder(ex.getMessage())
                                    .phone(ex.getCause().getMessage())
                                    .time(ex.getLocalizedMessage())
                                .build()));
                    }
                });
    }

    /**
     * Find all orders for the shop.
     *
     * <p>Allowed for Shops. Returns a list of orders corresponding to the pharmacy.</p>
     *
     * @param userDetails the authenticated user details
     * @return a response entity containing a list of order DTOs
     */
    @GetMapping("/get/all")
    @Operation(summary = "Find all orders for the shop",
            description = "Allowed for Shops. Returns list of orders corresponding to the pharmacy",
            tags = {"Get"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad request. Something wrong"),
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "500", description = "Some error. Error message should be in response body",
                    content = @Content(
                            schema = @Schema(implementation = ShopInfoCacheDTO.class),
                            examples = @ExampleObject(value = """
                    [
                                   {
                                     "phone": "Error message 1",
                                     "time": "Error message 2",
                                     "idOrder": "Error message 3",
                                   }
                                 ]
                """)
                    ))
    })
    public CompletableFuture<ResponseEntity<List<OrderDTO>>> getAllOrdersForShop(@AuthenticationPrincipal UserDetails userDetails) {
        return additionalService.getAllOrdersForShop(userDetails.getUsername())
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(List.of(OrderDTO
                            .builder()
                            .idOrder(ex.getMessage())
                            .phone(ex.getCause().getMessage())
                            .time(ex.getLocalizedMessage())
                            .build())
                        )
                );
    }


}
