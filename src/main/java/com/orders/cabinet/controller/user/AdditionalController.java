package com.orders.cabinet.controller.user;

import com.orders.cabinet.event.Timed;
import com.orders.cabinet.exception.NoSuchShopException;
import com.orders.cabinet.model.api.PriceList;
import com.orders.cabinet.model.api.dto.OrderDTO;
import com.orders.cabinet.model.db.dto.ShopInfoCacheDTO;
import com.orders.cabinet.service.AdditionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@Slf4j
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
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "204", description = "No orders were found",
                    content = @Content(
                    schema = @Schema(implementation = OrderDTO.class),
                    examples = @ExampleObject(value = """
                    [
                                   {
                                     "errorMessage": "Error message"
                                   }
                                 ]
                """)
            )),
            @ApiResponse(responseCode = "400", description = "Bad request. Something wrong",
                    content = @Content(
                            schema = @Schema(implementation = OrderDTO.class),
                            examples = @ExampleObject(value = """
                    [
                                   {
                                     "errorMessage": "Error message"
                                   }
                    ]
                """)
                    )),
            @ApiResponse(responseCode = "401", description = "Not authorized",
                    content = @Content(
                            examples = @ExampleObject(value = "     ")
                    )),
            @ApiResponse(responseCode = "405", description = "Method Not Allowed - The request method is known by the server but is not supported by the target resource.",
                    content = @Content(
                            examples = @ExampleObject(value = """
                {
                    "timestamp": "2024-07-29T13:41:00.000+00:00",
                    "status": 405,
                    "error": "Method Not Allowed",
                    "path": "/more/get-prop-by-string"
                }
                """)
                    )),
            @ApiResponse(responseCode = "418", description = "I'm a teapot - The server refuses the attempt to brew coffee with a teapot. User forgot to specify User-Agent for the request",
                    content = @Content(
                            examples = @ExampleObject(value = """
                {
                    "timestamp": "2024-07-29T13:38:56.583+00:00",
                    "status": 418,
                    "error": "I'm a teapot",
                    "path": "/more/get-prop-by-string"
                }
                """)
                    )),
            @ApiResponse(responseCode = "424", description = "Failed Dependency - Inappropriate User-Agent for the request..",
                    content = @Content(
                            examples = @ExampleObject(value = """
                {
                    "timestamp": "2024-07-29T13:39:13.642+00:00",
                    "status": 424,
                    "error": "Failed Dependency",
                    "path": "/more/get-prop-by-string"
                }
                """)
                    )),
            @ApiResponse(responseCode = "500", description = "Some error. Error message should be in response body",
                    content = @Content(
                            schema = @Schema(implementation = OrderDTO.class),
                            examples = @ExampleObject(value = """
                    [
                                   {
                                     "errorMessage": "Error message"
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
                                    .errorMessage(ex.getLocalizedMessage())
                                .build()));
                    } else if (cause instanceof NoSuchElementException || cause instanceof IllegalArgumentException) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of(OrderDTO
                                .builder()
                                    .errorMessage(ex.getLocalizedMessage())
                                .build()));
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of(OrderDTO
                                .builder()
                                    .errorMessage(ex.getLocalizedMessage())
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
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(
                            schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request. Something wrong"),
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "405", description = "Method Not Allowed - The request method is known by the server but is not supported by the target resource.",
                    content = @Content(
                            examples = @ExampleObject(value = """
                {
                    "timestamp": "2024-07-29T13:41:00.000+00:00",
                    "status": 405,
                    "error": "Method Not Allowed",
                    "path": "/more/get-prop-by-string"
                }
                """)
                    )),
            @ApiResponse(responseCode = "418", description = "I'm a teapot - The server refuses the attempt to brew coffee with a teapot. User forgot to specify User-Agent for the request",
                    content = @Content(
                            examples = @ExampleObject(value = """
                {
                    "timestamp": "2024-07-29T13:38:56.583+00:00",
                    "status": 418,
                    "error": "I'm a teapot",
                    "path": "/more/get-prop-by-string"
                }
                """)
                    )),
            @ApiResponse(responseCode = "424", description = "Failed Dependency - Inappropriate User-Agent for the request.",
                    content = @Content(
                            examples = @ExampleObject(value = """
                {
                    "timestamp": "2024-07-29T13:39:13.642+00:00",
                    "status": 424,
                    "error": "Failed Dependency",
                    "path": "/more/get-prop-by-string"
                }
                """)
                    )),
            @ApiResponse(responseCode = "500", description = "Some error. Error message should be in response body",
                    content = @Content(
                            schema = @Schema(implementation = OrderDTO.class),
                            examples = @ExampleObject(value = """
                    [
                                   {
                                     "errorMessage": "Error message"
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
                                .errorMessage(ex.getLocalizedMessage())
                            .build())
                        )
                );
    }
    @Timed
    @PostMapping("/get-prop-by-string")
    @Operation(summary = "Find all drugs in shop by first symbols",
            description = "Allowed for Shops. Returns list of drugs corresponding to the pharmacy and first symbols",
            tags = {"Get"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = PriceList.class)
                            ))),
            @ApiResponse(responseCode = "400", description = "Bad request. Something wrong"),
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "405", description = "Method Not Allowed - The request method is known by the server but is not supported by the target resource.",
                    content = @Content(
                            examples = @ExampleObject(value = """
                {
                    "timestamp": "2024-07-29T13:41:00.000+00:00",
                    "status": 405,
                    "error": "Method Not Allowed",
                    "path": "/more/get-prop-by-string"
                }
                """)
                    )),
            @ApiResponse(responseCode = "418", description = "I'm a teapot - The server refuses the attempt to brew coffee with a teapot. User forgot to specify User-Agent for the request",
                    content = @Content(
                            examples = @ExampleObject(value = """
                {
                    "timestamp": "2024-07-29T13:38:56.583+00:00",
                    "status": 418,
                    "error": "I'm a teapot",
                    "path": "/more/get-prop-by-string"
                }
                """)
                    )),
            @ApiResponse(responseCode = "424", description = "Failed Dependency - Inappropriate User-Agent for the request.",
                    content = @Content(
                            examples = @ExampleObject(value = """
                {
                    "timestamp": "2024-07-29T13:39:13.642+00:00",
                    "status": 424,
                    "error": "Failed Dependency",
                    "path": "/more/get-prop-by-string"
                }
                """)
                    )),
            @ApiResponse(responseCode = "500", description = "Some error. Error message should be in response body",
                    content = @Content(
                            examples = @ExampleObject(value = """
                    [
                                   {
                                     "errorMessage": "Error message"
                                   }
                                 ]
                """)
                    ))
    })
    public CompletableFuture<ResponseEntity<List<PriceList>>> getPropByString(@AuthenticationPrincipal UserDetails userDetails, @NotEmpty @RequestBody String name) {
        return additionalService.getDrugByName(userDetails.getUsername(), name)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause();
                    if (cause instanceof IllegalArgumentException) {
                        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(List.of(PriceList
                                .builder()
                                    .errorMessage(ex.getLocalizedMessage())
                                .build()));
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of(PriceList
                                .builder()
                                    .errorMessage(ex.getLocalizedMessage())
                                .build()));
                    }
                });
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("errorMessage", "Required request body is missing or invalid");

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
