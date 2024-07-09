package com.orders.cabinet.controller.user;

import com.orders.cabinet.model.login.LoginDTO;
import com.orders.cabinet.service.LoginService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("${user.login.basePath}")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoginController {

    LoginService loginService;

    @PostMapping("/")
    public CompletableFuture<Boolean> login(@RequestBody LoginDTO loginDto) {
        return loginService.authentication(loginDto);
    }
}
