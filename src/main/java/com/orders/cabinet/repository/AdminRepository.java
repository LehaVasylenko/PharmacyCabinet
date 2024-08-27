package com.orders.cabinet.repository;

import com.orders.cabinet.model.api.Order;
import com.orders.cabinet.model.db.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    /**
     * Method to get all info about Administrator
     * @param username - username of Administrator
     * @return {@link Optional}<{@link Admin}> with Admin info
     */
    Optional<Admin> findByUsername(String username);

    /**
     * Method check if Admin with such username exists in DB
     * @param username - name of Admin
     * @return true - if admin with such username exists in DB
     */
    boolean existsByUsername(String username);
}
