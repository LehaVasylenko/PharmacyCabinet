package com.orders.cabinet.controller.user;


import com.orders.cabinet.model.api.dto.ControllerDTO;
import com.orders.cabinet.model.api.dto.OrderDTO;
import com.orders.cabinet.service.UpdateOrderService;
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
public class OrderManipulationController {

    UpdateOrderService service;

    @GetMapping("${user.orders.new}")
    public CompletableFuture<List<OrderDTO>> getNewOrders(@AuthenticationPrincipal UserDetails userDetails) {
        return service.getOrdersWithOnlyNewStateByShopId(userDetails.getUsername());
    }

    @PostMapping("${user.orders.confirm}")
    public CompletableFuture<ResponseEntity<String>> confirmOrder(@AuthenticationPrincipal UserDetails userDetails,
                                                                  @Valid @RequestBody ControllerDTO controllerDTO) {
        return service.confirmOrder(userDetails.getUsername(), controllerDTO)
                .thenApply(result -> ResponseEntity.status(201).body("Confirmed"))
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof SQLException) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getCause().getMessage());
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
                    }
                });
    }

    @PostMapping("${user.orders.complete}")
    public CompletableFuture<ResponseEntity<String>> completeOrder(@AuthenticationPrincipal UserDetails userDetails,
                                                                   @Valid @RequestBody ControllerDTO controllerDTO) {
        return service.completeOrder(userDetails.getUsername(), controllerDTO)
                .thenApply(result -> ResponseEntity.status(201).body("Completed"))
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof SQLException) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getCause().getMessage());
                    } else if (ex.getCause() instanceof NoSuchElementException) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getCause().getMessage());
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
                    }
                });
    }

    @PostMapping("${user.orders.cancel}")
    public CompletableFuture<ResponseEntity<String>> cancelOrder(@AuthenticationPrincipal UserDetails userDetails,
                                                                 @Valid @RequestBody ControllerDTO controllerDTO) {
        return service.cancelOrder(userDetails.getUsername(), controllerDTO)
                .thenApply(result -> ResponseEntity.status(201).body("Canceled"))
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof SQLException) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getCause().getMessage());
                    } else if (ex.getCause() instanceof NoSuchElementException) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getCause().getMessage());
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
                    }
                });
    }

}
