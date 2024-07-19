package com.orders.cabinet.controller.user;


import com.orders.cabinet.exception.NoSuchShopException;
import com.orders.cabinet.exception.OrderOutOfDateException;
import com.orders.cabinet.model.api.dto.ControllerDTO;
import com.orders.cabinet.model.api.dto.OrderDTO;
import com.orders.cabinet.model.db.dto.ShopInfoCacheDTO;
import com.orders.cabinet.service.UpdateOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("${user.orders}")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Order Manipulation Controller", description = "Allows shops to get new orders, confirm, cancel and complete orders")
public class OrderManipulationController {

    UpdateOrderService service;

    @GetMapping("${user.orders.new}")
    @Operation(summary = "Get New orders for shop",
            description = "Allowed for shops. Returns a list of orders for the pharmacy that made the request",
            tags = {"Get"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(
                            schema = @Schema(implementation = OrderDTO.class)
                    )),
            @ApiResponse(responseCode = "204", description = "No orders for shop were found",
                    content = @Content(
                            examples = @ExampleObject(value = "    ")
                    )),
            @ApiResponse(responseCode = "400", description = "Bad request. Something wrong",
                    content = @Content(
                            schema = @Schema(implementation = OrderDTO.class),
                            examples = @ExampleObject(value = """
                    {
                      "errorMessage": "Houston, we have problems!"
                    }
                """)
                    )),
            @ApiResponse(responseCode = "401", description = "Not authorized",
                    content = @Content(
                            examples = @ExampleObject(value = "    ")
                    )),
            @ApiResponse(responseCode = "500", description = "Some error. Need to explore",
                    content = @Content(
                            schema = @Schema(implementation = OrderDTO.class),
                            examples = @ExampleObject(value = """
                    {
                      "errorMessage": "Any exception messages here"
                    }
                """)
                    ))
    })
    public CompletableFuture<ResponseEntity<List<OrderDTO>>> getNewOrders(@AuthenticationPrincipal UserDetails userDetails) {
        return service.getOrdersWithOnlyNewStateByShopId(userDetails.getUsername())
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof NoSuchShopException) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                    } else {
                        return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(List.of(OrderDTO
                                        .builder()
                                        .errorMessage(ex.getMessage())
                                        .build()));
                    }
                });
    }

    @PostMapping("${user.orders.confirm}")
    @Operation(summary = "Confirm order",
            description = "Allowed for shops. Sent when the pharmacy confirms an order received by it",
            tags = {"Confirm"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order confirmed",
                    content = @Content(
                            examples = @ExampleObject(value = "Confirmed")
                    )),
            @ApiResponse(responseCode = "204", description = "No orders for shop were found",
                    content = @Content(
                            examples = @ExampleObject(value = "    ")
                    )),
            @ApiResponse(responseCode = "400", description = "Bad request. Something wrong",
                    content = @Content(
                            examples = @ExampleObject(value = "Houston, we have problems!")
                    )),
            @ApiResponse(responseCode = "401", description = "Not authorized",
                    content = @Content(
                            examples = @ExampleObject(value = "    ")
                    )),
            @ApiResponse(responseCode = "405", description = "Trying to change statuses 'Completed' or 'Canceled'",
                    content = @Content(
                            examples = @ExampleObject(value = "Houston, we have problems!")
                    )),
            @ApiResponse(responseCode = "409", description = "I don't know how this could happens",
                    content = @Content(
                            examples = @ExampleObject(value = "Houston, we have problems!")
                    )),
            @ApiResponse(responseCode = "418", description = "Order expired",
                    content = @Content(
                            examples = @ExampleObject(value = "Houston, we have problems!")
                    )),
            @ApiResponse(responseCode = "500", description = "Some error. Need to explore",
                    content = @Content(
                            examples = @ExampleObject(value = "Houston, we have problems!")
                    ))
    })
    public CompletableFuture<ResponseEntity<String>> confirmOrder(@AuthenticationPrincipal UserDetails userDetails,
                                                                  @Valid @RequestBody ControllerDTO controllerDTO) {
        return service.confirmOrder(userDetails.getUsername(), controllerDTO)
                .thenApply(result -> ResponseEntity.status(201).body("Confirmed"))
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof SQLException) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getCause().getMessage());
                    } else if (ex.getCause() instanceof NoSuchElementException) {
                        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ex.getCause().getMessage());
                    }  else if (ex.getCause() instanceof IllegalStateException || ex.getCause() instanceof IllegalArgumentException) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getCause().getMessage());
                    } else if (ex.getCause() instanceof OrderOutOfDateException) {
                        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(ex.getCause().getMessage());
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
                    }
                });
    }

    @PostMapping("${user.orders.complete}")
    @Operation(summary = "Complete order",
            description = "Allowed for shops. Sent when a client redeems an order at a pharmacy",
            tags = {"Complete"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order confirmed",
                    content = @Content(
                            examples = @ExampleObject(value = "Confirmed")
                    )),
            @ApiResponse(responseCode = "204", description = "No orders for shop were found",
                    content = @Content(
                            examples = @ExampleObject(value = "    ")
                    )),
            @ApiResponse(responseCode = "400", description = "Bad request. Something wrong",
                    content = @Content(
                            examples = @ExampleObject(value = "Houston, we have problems!")
                    )),
            @ApiResponse(responseCode = "401", description = "Not authorized",
                    content = @Content(
                            examples = @ExampleObject(value = "    ")
                    )),
            @ApiResponse(responseCode = "405", description = "Trying to change statuses 'Completed' or 'Canceled'",
                    content = @Content(
                            examples = @ExampleObject(value = "Houston, we have problems!")
                    )),
            @ApiResponse(responseCode = "409", description = "I don't know how this could happens",
                    content = @Content(
                            examples = @ExampleObject(value = "Houston, we have problems!")
                    )),
            @ApiResponse(responseCode = "418", description = "Order expired",
                    content = @Content(
                            examples = @ExampleObject(value = "Houston, we have problems!")
                    )),
            @ApiResponse(responseCode = "500", description = "Some error. Need to explore",
                    content = @Content(
                            examples = @ExampleObject(value = "Houston, we have problems!")
                    ))
    })
    public CompletableFuture<ResponseEntity<String>> completeOrder(@AuthenticationPrincipal UserDetails userDetails,
                                                                   @Valid @RequestBody ControllerDTO controllerDTO) {
        return service.completeOrder(userDetails.getUsername(), controllerDTO)
                .thenApply(result -> ResponseEntity.status(201).body("Completed"))
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof SQLException) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getCause().getMessage());
                    } else if (ex.getCause() instanceof NoSuchElementException) {
                        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ex.getCause().getMessage());
                    }  else if (ex.getCause() instanceof IllegalStateException || ex.getCause() instanceof IllegalArgumentException) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getCause().getMessage());
                    } else if (ex.getCause() instanceof OrderOutOfDateException) {
                        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(ex.getCause().getMessage());
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
                    }
                });
    }

    @PostMapping("${user.orders.cancel}")
    @Operation(summary = "Cancel order",
            description = "Allowed for shops. Sent when a pharmacy cancels an order for one reason or another",
            tags = {"Cancel"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order confirmed",
                    content = @Content(
                            examples = @ExampleObject(value = "Confirmed")
                    )),
            @ApiResponse(responseCode = "204", description = "No orders for shop were found",
                    content = @Content(
                            examples = @ExampleObject(value = "    ")
                    )),
            @ApiResponse(responseCode = "400", description = "Bad request. Something wrong",
                    content = @Content(
                            examples = @ExampleObject(value = "Houston, we have problems!")
                    )),
            @ApiResponse(responseCode = "401", description = "Not authorized",
                    content = @Content(
                            examples = @ExampleObject(value = "    ")
                    )),
            @ApiResponse(responseCode = "405", description = "Trying to change statuses 'Completed' or 'Canceled'",
                    content = @Content(
                            examples = @ExampleObject(value = "Houston, we have problems!")
                    )),
            @ApiResponse(responseCode = "409", description = "I don't know how this could happens",
                    content = @Content(
                            examples = @ExampleObject(value = "Houston, we have problems!")
                    )),
            @ApiResponse(responseCode = "418", description = "Order expired",
                    content = @Content(
                            examples = @ExampleObject(value = "Houston, we have problems!")
                    )),
            @ApiResponse(responseCode = "500", description = "Some error. Need to explore",
                    content = @Content(
                            examples = @ExampleObject(value = "Houston, we have problems!")
                    ))
    })
    public CompletableFuture<ResponseEntity<String>> cancelOrder(@AuthenticationPrincipal UserDetails userDetails,
                                                                 @Valid @RequestBody ControllerDTO controllerDTO) {
        return service.cancelOrder(userDetails.getUsername(), controllerDTO)
                .thenApply(result -> ResponseEntity.status(201).body("Canceled"))
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof SQLException) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getCause().getMessage());
                    } else if (ex.getCause() instanceof NoSuchElementException) {
                        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ex.getCause().getMessage());
                    }  else if (ex.getCause() instanceof IllegalStateException || ex.getCause() instanceof IllegalArgumentException) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getCause().getMessage());
                    } else if (ex.getCause() instanceof OrderOutOfDateException) {
                        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(ex.getCause().getMessage());
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
                    }
                });
    }

}
