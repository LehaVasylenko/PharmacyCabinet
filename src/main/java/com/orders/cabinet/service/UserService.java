package com.orders.cabinet.service;

import com.orders.cabinet.model.Role;
import com.orders.cabinet.model.db.Admin;
import com.orders.cabinet.model.db.Shops;
import com.orders.cabinet.repository.AdminRepository;
import com.orders.cabinet.repository.ShopRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements UserDetailsService {

    AdminRepository adminRepository;
    ShopRepository shopRepository;
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info(username);
        Optional<Shops> maybeShop = shopRepository.findById(username);
        if (maybeShop.isPresent()) {
            Shops shop = maybeShop.get();
            return new User(shop.getShopId(), shop.getPassword(), getAuthorities(shop.getRole()));
        }

        Optional<Admin> maybeAdmin = adminRepository.findByUsername(username);
        if (maybeAdmin.isPresent()) {
            Admin admin = maybeAdmin.get();
            log.info(admin.toString());
            return new User(admin.getUsername(), admin.getPassword(), getAuthorities(admin.getRole()));
        } else
            throw new UsernameNotFoundException("User " + username + " not found!");
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Role role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @PostConstruct
    public void createDefaultAdmin() {
        String defaultAdminName = "admin";

        if (!adminRepository.existsByUsername(defaultAdminName)) {
           Admin defaultAdmin = Admin.builder()
                   .username(defaultAdminName)
                   .password(passwordEncoder.encode(defaultAdminName))
                   .role(Role.ADMIN)
                   .build();
           adminRepository.save(defaultAdmin);
           log.info("Default admin created: {}", defaultAdmin);
        } else log.info("Default admin already exists!");
    }
}
