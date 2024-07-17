package com.orders.cabinet.controller.user;

import com.orders.cabinet.exception.NoSuchShopException;
import com.orders.cabinet.exception.PasswordMissmatchException;
import com.orders.cabinet.model.db.dto.ShopInfoCacheDTO;
import com.orders.cabinet.model.login.LoginDTO;
import com.orders.cabinet.service.LoginService;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("${user.login.basePath}")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Login Controller", description = "Allows users to login and log out")
public class LoginController {

    LoginService loginService;

    @PostMapping("/login")
    @Operation(summary = "Login for Shop",
            description = "Allowed for all users. Returns data corresponding to the pharmacy with the specified ID from https://api.geoapteka.com.ua/show-shops",
            tags = {"LogIn"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(
                            schema = @Schema(implementation = ShopInfoCacheDTO.class),
                            examples = @ExampleObject(value = """
                    {
                      "shopId": "124586",
                      "corpId": "9268",
                      "corpName": "Kryzhopyl Farmacy",
                      "name": "Apteka 11",
                      "mark": "Kryzhopyl Vitaminkas",
                      "area": "Kyivska",
                      "city": "Kyiv",
                      "street": "1 Bankova str",
                      "update": "2024-07-16T14:11:54.270Z",
                      "openHours": "Mo-Fr 07:45-21:15 Sa 07:45-20:15 Su 08:45-20:15"
                    }
                """)
                    )),
            @ApiResponse(responseCode = "400", description = "Bad request. Something wrong with field data types",
                    content = @Content(
                            schema = @Schema(implementation = ShopInfoCacheDTO.class),
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
            @ApiResponse(responseCode = "404", description = "No shop with such ID in https://api.geoapteka.com.ua/show-shops",
                    content = @Content(
                            schema = @Schema(implementation = ShopInfoCacheDTO.class),
                            examples = @ExampleObject(value = """
                    {
                      "errorMessage": "No such shop 11223344 in Geoapteka DB!"
                    }
                """)
                    )),
            @ApiResponse(responseCode = "500", description = "Some error. Need to explore",
                    content = @Content(
                            schema = @Schema(implementation = ShopInfoCacheDTO.class),
                            examples = @ExampleObject(value = """
                    {
                      "errorMessage": "Any exception messages here"
                    }
                """)
                    ))
    })
    public CompletableFuture<ResponseEntity<ShopInfoCacheDTO>> login(@RequestBody LoginDTO loginDto) {
        return loginService.authentication(loginDto)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof UsernameNotFoundException || ex.getCause() instanceof PasswordMissmatchException) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ShopInfoCacheDTO
                                .builder()
                                    .errorMessage(ex.getCause().getMessage())
                                .build());
                    } else if (ex.getCause() instanceof NoSuchShopException) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ShopInfoCacheDTO
                                .builder()
                                    .errorMessage(ex.getCause().getMessage())
                                .build());
                    } else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ShopInfoCacheDTO
                            .builder()
                                .errorMessage(ex.getCause().getMessage())
                            .build());
                });
    }

    @PostMapping("/logout")
    @Operation(summary = "LogOut for Shop",
            description = "Allowed for all users. Returns data corresponding to the pharmacy with the specified ID from https://api.geoapteka.com.ua/show-shops",
            tags = {"LogOut"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "204", description = "Shop already logged out"),
            @ApiResponse(responseCode = "400", description = "Bad request. Something wrong"),
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "404", description = "No shop with such ID was found"),
            @ApiResponse(responseCode = "500", description = "Some error. Error message should be in response body")
    })
    public CompletableFuture<ResponseEntity<String>> logout(@AuthenticationPrincipal UserDetails userDetails) {
        return loginService.logOut(userDetails.getUsername())
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof UsernameNotFoundException) {
                        return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(ex.getMessage());
                    } else if (ex.getCause() instanceof NoSuchShopException) {
                        return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ex.getMessage());
                    } else if (ex.getCause() instanceof IllegalStateException) {
                        return ResponseEntity
                                .status(HttpStatus.NO_CONTENT)
                                .body(ex.getMessage());
                    } else return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ex.getMessage());
                });
    }
}
