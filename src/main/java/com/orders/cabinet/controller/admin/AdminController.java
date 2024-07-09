package com.orders.cabinet.controller.admin;

import com.orders.cabinet.exception.NoSuchShopException;
import com.orders.cabinet.model.db.dto.AddShopDTO;
import com.orders.cabinet.model.db.dto.CorpDTO;
import com.orders.cabinet.model.db.dto.ShopsDTO;
import com.orders.cabinet.service.AdminService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminController {

    AdminService service;

    @PostMapping("${main.module.corps.add}")
    public CompletableFuture<ResponseEntity<String>> addCorp(@Valid @RequestBody List<CorpDTO> corpDTO) {
        return service.saveCorp(corpDTO)
                .thenApply(result -> ResponseEntity.status(201).body("Corps saved successfully"))
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof SQLException) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getCause().getMessage());
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
                    }
                });
    }

    @PostMapping("/corp/edit/{corpId}")
    public ResponseEntity<?> editCorp(@Valid @RequestBody CorpDTO corpDTO, @RequestParam String corpId) {
        service.editCorpById(corpId, corpDTO);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("${main.module.shops.add}")
    public CompletableFuture<ResponseEntity<String>> addShop(@Valid @RequestBody List<AddShopDTO> shop) throws SQLException {
        return service.saveShop(shop)
                .thenApply(result -> ResponseEntity.status(201).body("Shops saved successfully"))
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof SQLException) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getCause().getMessage());
                    } else if (ex.getCause() instanceof NoSuchShopException) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getCause().getMessage());
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
                    }
                });
    }

    @PostMapping("/admin/add")
    public CompletableFuture<ResponseEntity<String>> addAdmin(@Valid @RequestBody ShopsDTO shopsDTO) throws SQLException {
        return service.saveAdmin(shopsDTO)
                .thenApply(result -> ResponseEntity.status(201).body("Admin saved successfully"))
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof SQLException) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getCause().getMessage());
                    } else if (ex.getCause() instanceof NoSuchShopException) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getCause().getMessage());
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
                    }
                });
    }
}
