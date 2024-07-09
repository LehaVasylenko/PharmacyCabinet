package com.orders.cabinet.service;

import com.orders.cabinet.exception.PasswordMissmatchException;
import com.orders.cabinet.model.login.LoginDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoginService {
    PasswordEncoder passwordEncoder;
    UserService userService;

    @Async
    public CompletableFuture<Boolean> authentication(LoginDTO loginDto) {
        UserDetails userDetails = null;
        try {
            userDetails = userService.loadUserByUsername(loginDto.getShopId());
        } catch (UsernameNotFoundException ex) {
            return CompletableFuture.failedFuture(ex);
        }

        if (!passwordEncoder.matches(loginDto.getPassword(), userDetails.getPassword())) {
            return CompletableFuture.failedFuture(new PasswordMissmatchException("Wrong password!"));
        }

        return CompletableFuture.completedFuture(true);
    }
}
