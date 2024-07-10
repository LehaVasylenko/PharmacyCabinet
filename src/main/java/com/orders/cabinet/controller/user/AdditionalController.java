package com.orders.cabinet.controller.user;

import com.orders.cabinet.exception.NoSuchShopException;
import com.orders.cabinet.service.AdditionalService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("${user.additional}")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdditionalController {
    AdditionalService additionalService;

    @PostMapping("/get/last")
    public CompletableFuture<? extends ResponseEntity<?>> findByLast4Symbol(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestBody String last) {
        return additionalService.getOrderBy4LastSymbols(userDetails.getUsername(), last)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause();
                    if (cause instanceof NoSuchShopException) {
                        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
                    } else if (cause instanceof NoSuchElementException) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                });
    }

    @GetMapping("/get/all")
    public CompletableFuture<? extends ResponseEntity<?>> getAllOrdersForShop(@AuthenticationPrincipal UserDetails userDetails) {
        return additionalService.getAllOrdersForShop(userDetails.getUsername())
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }


}
