package com.orders.cabinet.controller.user;

import com.orders.cabinet.exception.NoSuchShopException;
import com.orders.cabinet.exception.PasswordMissmatchException;
import com.orders.cabinet.model.db.dto.ShopInfoCacheDTO;
import com.orders.cabinet.model.login.LoginDTO;
import com.orders.cabinet.service.LoginService;
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

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("${user.login.basePath}")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoginController {

    LoginService loginService;

    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<ShopInfoCacheDTO>> login(@RequestBody LoginDTO loginDto) {
        return loginService.authentication(loginDto)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof UsernameNotFoundException || ex.getCause() instanceof PasswordMissmatchException) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ShopInfoCacheDTO
                                .builder()
                                .shopId("Wrong credentials!")
                                .build());
                    } else if (ex.getCause() instanceof NoSuchShopException) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ShopInfoCacheDTO
                                .builder()
                                .shopId(ex.getCause().getMessage())
                                .build());
                    } else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ShopInfoCacheDTO
                            .builder()
                            .shopId(ex.getCause().getMessage())
                            .build());
                });
    }

    @PostMapping("/logout")
    public CompletableFuture<? extends ResponseEntity<String>> logout(@AuthenticationPrincipal UserDetails userDetails) {
        return loginService.logOut(userDetails.getUsername())
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ex.getMessage()));
    }
}
