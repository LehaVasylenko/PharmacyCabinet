package com.orders.cabinet.service;

import com.orders.cabinet.event.Timed;
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
/**
 * Service for managing user details and authentication.
 * <p>This service implements the {@link UserDetailsService} interface to provide user details for authentication.</p>
 *
 * <p>It also creates a default admin user if none exists.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements UserDetailsService {

    AdminRepository adminRepository;
    ShopRepository shopRepository;
    PasswordEncoder passwordEncoder;

    /**
     * Loads user details by username.
     * <p>It first tries to load the user from the {@link ShopRepository}. If not found, it then tries to load from
     * the {@link AdminRepository}.</p>
     *
     * @param username the username of the user to be loaded.
     * @return a {@link UserDetails} object representing the user.
     * @throws UsernameNotFoundException if no user with the provided username is found.
     */

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

    /**
     * Gets the authorities for the given role.
     *
     * @param role the {@link Role} of the user.
     * @return a collection of {@link GrantedAuthority} objects representing the user's roles.
     */
    private Collection<? extends GrantedAuthority> getAuthorities(Role role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    /**
     * Creates a default admin user if one does not already exist.
     * <p>This method is called after the bean's properties have been initialized.</p>
     */
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

//    @PostConstruct
//    public void createDefaultShop() {
//        String defaultShopName = "111111";
//
//        if (shopRepository.getShopByShopId(defaultShopName).isEmpty()) {
//            Shops defaultShop = Shops.builder()
//                    .shopId(defaultShopName)
//                    .password(passwordEncoder.encode(defaultShopName))
//                    .role(Role.SHOP)
//                    .logged(true)
//                    .build();
//            shopRepository.save(defaultShop);
//            log.info("Default shop created: {}", defaultShop);
//        } else log.info("Default shop already exists!");
//    }
}
