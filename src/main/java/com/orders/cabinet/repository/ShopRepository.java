package com.orders.cabinet.repository;

import com.orders.cabinet.model.db.Corp;
import com.orders.cabinet.model.db.Shops;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shops, String> {
    @Query(value = "SELECT * FROM shops WHERE shop_id = ?1", nativeQuery = true)
    Optional<Shops> getShopByShopId(String shopId);

    @Query("SELECT s.corp FROM Shops s WHERE s.shopId = :shopId")
    Optional<Corp> findCorpByShopId(@Param("shopId") String shopId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Shops s WHERE s.corp.corpId = :corpId")
    void deleteByCorpId(String corpId);

    @Modifying
    @Transactional
    @Query("UPDATE Shops s SET s.password = :password WHERE s.shopId = :shopId")
    void updatePassword(@Param("shopId") String shopId, @Param("password") String password);

//    @Modifying
//    @Transactional
//    @Query("UPDATE Shops s SET s.lastLogin = :lastLogin WHERE s.shopId = :shopId")
//    void updateLastLogin(@Param("shopId") String shopId, @Param("lastLogin") Date lastLogin);
//
//    @Modifying
//    @Transactional
//    @Query("UPDATE Shops s SET s.loggedIn = :loggedIn WHERE s.shopId = :shopId")
//    void updateLoggedIn(@Param("shopId") String shopId, @Param("loggedIn") Boolean loggedIn);

}
