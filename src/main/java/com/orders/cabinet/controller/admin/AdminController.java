package com.orders.cabinet.controller.admin;

import com.orders.cabinet.exception.NoSuchShopException;
import com.orders.cabinet.model.db.dto.*;
import com.orders.cabinet.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
/**
 * Controller for admin operations related to shops and corporations.
 *
 * <p>This controller provides endpoints to manage shops and corporations, including adding, editing, and deleting them.</p>
 *
 * @version 1.0
 * @since 2024-07-19
 *
 * @see AdminService
 * @see CorpDTO
 * @see AddShopDTO
 * @see AdminDTO
 * @see ShopsDTO
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 */
@Slf4j
@RestController
@RequestMapping("${main.module.admin.panel}")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Admin Controller", description = "Allow operations to manage shops and corporations")
public class AdminController {

    AdminService service;

    /**
     * Adds a list of corporations.
     *
     * @param corpDTO the list of corporations to add
     * @return a response entity indicating the result of the operation
     */
    @PostMapping("${main.module.corps.add}")
    @Operation(summary = "Add corporation",
            description = "Only a user with Administrator access level can add",
            tags = {"Add"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad request. Something wrong with field data types"),
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "403", description = "Wrong access level"),
            @ApiResponse(responseCode = "409", description = "Such corp already exists"),
            @ApiResponse(responseCode = "500", description = "Some error. Error message should be in response body")
    })
    public CompletableFuture<ResponseEntity<String>> addCorp(@Valid @RequestBody List<CorpDTO> corpDTO) {
        return service.saveCorp(corpDTO)
                .thenApply(result -> ResponseEntity.status(201).body("Corps saved successfully"))
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof SQLException) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getCause().getMessage());
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getCause().getMessage());
                    }
                });
    }

    /**
     * Adds a list of shops.
     *
     * @param shop the list of shops to add
     * @return a response entity containing the result of the operation
     */
    @PostMapping("${main.module.shops.add}")
    @Operation(summary = "Add shop",
            description = "Only a user with Administrator access level can add",
            tags = {"Add"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad request. Something wrong with field data types"),
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "403", description = "Wrong access level"),
            @ApiResponse(responseCode = "404", description = "Shop with such ID doesn't exists in https://api.geoapteka.com.ua/show-shops"),
            @ApiResponse(responseCode = "409", description = "Such shop already exists"),
            @ApiResponse(responseCode = "500", description = "Some error. Error message should be in response body")
    })
    public CompletableFuture<ResponseEntity<List<ShopInfoCacheDTO>>> addShop(@Valid @RequestBody List<AddShopDTO> shop) {
        return service.saveShop(shop)
                .thenApply(result -> ResponseEntity.status(201).body(result))
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof SQLException) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(List.of(ShopInfoCacheDTO
                                .builder()
                                .errorMessage(ex.getCause().getMessage())
                                .build()));
                    } else if (ex.getCause() instanceof NoSuchShopException) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of(ShopInfoCacheDTO
                                .builder()
                                .errorMessage(ex.getCause().getMessage())
                                .build()));
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of(ShopInfoCacheDTO
                                .builder()
                                .errorMessage(ex.getCause().getMessage())
                                .build()));
                    }
                });
    }

    /**
     * Adds an administrator.
     *
     * @param adminDTO the administrator details to add
     * @return a response entity indicating the result of the operation
     */
    @PostMapping("${main.module.admin.add}")
    @Operation(summary = "Add administrator",
            description = "Only a user with Administrator access level can add",
            tags = {"Add"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad request. Something wrong with field data types"),
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "403", description = "Wrong access level"),
            @ApiResponse(responseCode = "409", description = "Admin with such username already exists"),
            @ApiResponse(responseCode = "500", description = "Some error. Error message should be in response body")
    })
    public CompletableFuture<ResponseEntity<String>> addAdmin(@Valid @RequestBody AdminDTO adminDTO) {
        return service.saveAdmin(adminDTO)
                .thenApply(result -> ResponseEntity.status(201).body("Admin saved successfully"))
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof SQLException) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getCause().getMessage());
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getCause().getMessage());
                    }
                });
    }

    /**
     * Edits a corporation by its ID.
     *
     * @param corpDTO the corporation details to edit
     * @param corpId the ID of the corporation to edit
     * @return a response entity indicating the result of the operation
     */
    @PostMapping("${main.module.corps.edit}/{corpId}")
    @Operation(summary = "Edit corporation",
            description = "Only a user with Administrator access level can add. You can edit not only all fields, but also some separately",
            tags = {"Edit"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad request. Something wrong with field data types"),
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "403", description = "Wrong access level"),
            @ApiResponse(responseCode = "500", description = "Some error. Error message should be in response body")
    })
    public CompletableFuture<ResponseEntity<String>> editCorp(@Valid @RequestBody CorpDTO corpDTO, @PathVariable String corpId) {

        return service.editCorpById(corpId, corpDTO)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage()));
    }

    /**
     * Edits a shop by its ID.
     *
     * @param password the new password for the shop
     * @param shopId the ID of the shop to edit
     * @return a response entity indicating the result of the operation
     */
    @PostMapping("${main.module.shops.edit}/{shopId}")
    @Operation(summary = "Edit shop",
            description = "Only a user with Administrator access level can add. Can be edited only password",
            tags = {"Edit"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad request. Something wrong with field data types"),
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "403", description = "Wrong access level"),
            @ApiResponse(responseCode = "500", description = "Some error. Error message should be in response body")
    })
    public CompletableFuture<ResponseEntity<String>> editShop(@RequestBody String password, @PathVariable String shopId) {
        return service.editShopById(shopId, password)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage()));
    }

    /**
     * Deletes a shop by its ID.
     *
     * @param shopId the ID of the shop to delete
     * @return a response entity indicating the result of the operation
     */
    @PostMapping("${main.module.shops.delete}")
    @Operation(summary = "Delete shop",
            description = "Only a user with Administrator access level can add. Also, all orders that were linked to the store will be deleted",
            tags = {"Delete"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad request. Something wrong with field data types"),
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "403", description = "Wrong access level"),
            @ApiResponse(responseCode = "500", description = "Some error. Error message should be in response body")
    })
    public CompletableFuture<ResponseEntity<String>> deleteShop(@RequestBody String shopId) {
        return service.deleteShop(shopId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage()));
    }

    /**
     * Deletes a corporation by its ID.
     *
     * @param corpId the ID of the corporation to delete
     * @return a response entity indicating the result of the operation
     */
    @PostMapping("${main.module.corps.delete}")
    @Operation(summary = "Delete corporation",
            description = "Only a user with Administrator access level can add. Also, all shops and orders that were linked to the corp will be deleted",
            tags = {"Delete"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad request. Something wrong with field data types"),
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "403", description = "Wrong access level"),
            @ApiResponse(responseCode = "500", description = "Some error. Error message should be in response body")
    })
    public CompletableFuture<ResponseEntity<String>> deleteCorp(@RequestBody String corpId) {
        return service.deleteCorp(corpId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage()));
    }

    /**
     * Gets a shop by its ID.
     *
     * @param shopId the ID of the shop to retrieve
     * @return a response entity containing the shop details
     */
    @GetMapping("${main.module.shops.get}/{shopId}")
    @Operation(summary = "Get shop by ID",
            description = "Only a user with Administrator access level can add.",
            tags = {"Get"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad request. Something wrong with field data types"),
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "403", description = "Wrong access level"),
            @ApiResponse(responseCode = "404", description = "Shop not found"),
            @ApiResponse(responseCode = "500", description = "Some error. Error message should be in response body")
    })
    public CompletableFuture<ResponseEntity<ShopsDTO>> getByShopId(@PathVariable String shopId) {
        return service.getShopById(shopId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof NoSuchShopException) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ShopsDTO
                                .builder()
                                .errorMessage(ex.getMessage())
                                .build());
                    } else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ShopsDTO
                            .builder()
                            .errorMessage(ex.getLocalizedMessage())
                            .build());
                });
    }
}
