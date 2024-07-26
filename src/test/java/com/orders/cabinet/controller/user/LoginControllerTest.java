package com.orders.cabinet.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orders.cabinet.exception.NoSuchShopException;
import com.orders.cabinet.model.db.dto.ShopInfoCacheDTO;
import com.orders.cabinet.model.login.LoginDTO;
import com.orders.cabinet.service.LoginService;
import com.orders.cabinet.util.PropertiesUtil;
import com.sun.security.auth.UserPrincipal;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.Authorization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class LoginControllerTest {

    @Mock
    LoginService service;

    @InjectMocks
    LoginController controller;

    MockMvc mockMvc;
    ObjectMapper mapper;

    String loginPath;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .addPlaceholderValue("user.login.basePath", "/user")
                .build();
        mapper = new ObjectMapper();
        loginPath = PropertiesUtil.get("user.login.basePath");
    }

    @Test
    void loginTestNegative() throws Exception {
        mockMvc.perform(post(loginPath + "/login")).andExpect(status().is4xxClientError());
    }

    @Test
    void loginTestPositive() throws Exception {
        ShopInfoCacheDTO test = ShopInfoCacheDTO.builder()
                .shopId("1")
                .area("1")
                .city("1")
                .mark("1")
                .corpId("1")
                .corpName("1")
                .name("1")
                .openHours("1")
                .update("1")
                .street("1")
                .build();

        when(service.authentication(any(LoginDTO.class))).thenReturn(CompletableFuture.completedFuture(test));

        MvcResult mvcResult = mockMvc.perform(post(loginPath + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(getLoginDTO())))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));

        verify(service, times(1)).authentication(any(LoginDTO.class));
    }

    @Test
    void loginTestUnauthorized() throws Exception {
        String errorMessafe = "Invalid credentials";
        when(service.authentication(any(LoginDTO.class))).thenReturn(CompletableFuture.failedFuture(new UsernameNotFoundException(errorMessafe)));

        String content = mapper.writeValueAsString(getLoginDTO());

        MvcResult mvcResult = mockMvc.perform(post(loginPath + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorMessage").value(errorMessafe));

        verify(service, times(1)).authentication(any(LoginDTO.class));
    }

    @Test
    void loginTestNotFound() throws Exception {
        String errorMessafe = "No such shop in Geoapteka DB!";
        when(service.authentication(any(LoginDTO.class))).thenReturn(CompletableFuture.failedFuture(new NoSuchShopException(errorMessafe)));

        String content = mapper.writeValueAsString(getLoginDTO());

        MvcResult mvcResult = mockMvc.perform(post(loginPath + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value(errorMessafe));

        verify(service, times(1)).authentication(any(LoginDTO.class));
    }

    @Test
    void loginTestInternalServerError() throws Exception {
        String errorMessafe = "Unexpected error";
        when(service.authentication(any(LoginDTO.class))).thenReturn(CompletableFuture.failedFuture(new RuntimeException(errorMessafe)));

        String content = mapper.writeValueAsString(getLoginDTO());

        MvcResult mvcResult = mockMvc.perform(post(loginPath + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorMessage").value(errorMessafe));

        verify(service, times(1)).authentication(any(LoginDTO.class));
    }

    private LoginDTO getLoginDTO() {
        return LoginDTO.builder()
                .shopId("12345")
                .password("azaza")
                .build();
    }

}