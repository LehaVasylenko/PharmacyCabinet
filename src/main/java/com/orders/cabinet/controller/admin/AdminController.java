package com.orders.cabinet.controller.admin;

import com.orders.cabinet.exception.NoSuchShopException;
import com.orders.cabinet.model.db.dto.*;
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
@RequestMapping("${main.module.admin.panel}")
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

    @PostMapping("${main.module.shops.add}")
    public CompletableFuture<ResponseEntity<List<ShopInfoCacheDTO>>> addShop(@Valid @RequestBody List<AddShopDTO> shop) {
        return service.saveShop(shop)
                .thenApply(result -> ResponseEntity.status(201).body(result))
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof SQLException) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(List.of(ShopInfoCacheDTO
                                .builder()
                                        .shopId(ex.getCause().getMessage())
                                .build()));
                    } else if (ex.getCause() instanceof NoSuchShopException) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of(ShopInfoCacheDTO
                                .builder()
                                .shopId(ex.getCause().getMessage())
                                .build()));
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of(ShopInfoCacheDTO
                                .builder()
                                .shopId(ex.getCause().getMessage())
                                .build()));
                    }
                });
    }

    @PostMapping("${main.module.admin.add}")
    public CompletableFuture<ResponseEntity<String>> addAdmin(@Valid @RequestBody AdminDTO adminDTO) {
        return service.saveAdmin(adminDTO)
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

    @PostMapping("${main.module.corps.edit}/{corpId}")
    public CompletableFuture<ResponseEntity<String>> editCorp(@Valid @RequestBody CorpDTO corpDTO, @PathVariable String corpId) {

        return service.editCorpById(corpId, corpDTO)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage()));
    }

    @PostMapping("${main.module.shops.edit}/{shopId}")
    public CompletableFuture<ResponseEntity<String>> editShop(@RequestBody String password, @PathVariable String shopId) {
        return service.editShopById(shopId, password)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage()));
    }

    @PostMapping("${main.module.shops.delete}")
    public CompletableFuture<ResponseEntity<String>> deleteShop(@RequestBody String shopId) {
        return service.deleteShop(shopId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage()));
    }

    @PostMapping("${main.module.corps.delete}")
    public CompletableFuture<ResponseEntity<String>> deleteCorp(@RequestBody String corpId) {
        return service.deleteCorp(corpId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage()));
    }

    @GetMapping("${main.module.shops.get}/{shopId}")
    public CompletableFuture<ResponseEntity<ShopsDTO>> getByShopId(@PathVariable String shopId) {
        return service.getShopById(shopId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
}
