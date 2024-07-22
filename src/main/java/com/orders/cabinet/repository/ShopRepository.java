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
/**
 * Repository interface for {@link Shops} entity.
 *
 * <p>This interface extends {@link JpaRepository} to provide CRUD operations for {@link Shops} entities.
 * It leverages Spring Data JPA to generate the necessary implementation at runtime.</p>
 *
 * <p>Custom queries are defined to perform specific operations such as finding shops by ID, updating passwords,
 * and deleting shops by corp ID.</p>
 *
 * @see Shops
 * @see JpaRepository
 *
 * @param <Shops> the type of the entity to handle
 * @param <String> the type of the entity's identifier
 *
 * @version 1.0
 * @since 2024-07-19
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 */
public interface ShopRepository extends JpaRepository<Shops, String> {

    /**
     * Finds a shop by its ID.
     *
     * @param shopId the ID of the shop to find
     * @return an {@link Optional} containing the found shop, or {@link Optional#empty()} if not found
     */
    @Query(value = "SELECT * FROM shops WHERE shop_id = ?1", nativeQuery = true)
    Optional<Shops> getShopByShopId(String shopId);

    /**
     * Finds a shop by its ID.
     *
     * @param shopId the ID of the shop to find
     * @return an {@link Optional} containing the found shop, or {@link Optional#empty()} if not found
     */
    @Query("SELECT s.corp FROM Shops s WHERE s.shopId = :shopId")
    Optional<Corp> findCorpByShopId(@Param("shopId") String shopId);

    /**
     * Deletes shops associated with a given corporation ID.
     *
     * <p>This method uses a custom query to delete shops based on their associated corporation ID.
     * It is marked with {@link Modifying} and {@link Transactional} annotations to ensure the operation
     * is executed within a transaction and modifies the database state.</p>
     *
     * @param corpId the ID of the corporation whose shops are to be deleted
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Shops s WHERE s.corp.corpId = :corpId")
    void deleteByCorpId(String corpId);

    /**
     * Updates the password for a shop.
     *
     * @param shopId the ID of the shop to update
     * @param password the new password to set
     */
    @Modifying
    @Transactional
    @Query("UPDATE Shops s SET s.password = :password WHERE s.shopId = :shopId")
    void updatePassword(@Param("shopId") String shopId, @Param("password") String password);

    /**
     * Updates the logged-in status for a shop.
     *
     * @param shopId the ID of the shop to update
     * @param loggedIn the new logged-in status to set
     */
    @Modifying
    @Transactional
    @Query("UPDATE Shops s SET s.logged = :loggedIn WHERE s.shopId = :shopId")
    void updateLoggedIn(@Param("shopId") String shopId, @Param("loggedIn") Boolean loggedIn);

    /**
     * Finds all shops that are currently logged in.
     *
     * @return a list of all shops that are logged in
     */
    List<Shops> findAllByLoggedTrue();

}
