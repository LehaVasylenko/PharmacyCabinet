package com.orders.cabinet.repository;

import com.orders.cabinet.model.db.Corp;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
/**
 * Repository interface for {@link Corp} entity.
 *
 * <p>This interface extends {@link JpaRepository} to provide CRUD operations for {@link Corp} entities.
 * Additionally, it declares custom query methods to retrieve and update data based on specific conditions.</p>
 *
 * <p>Each method is documented with its purpose and usage details.</p>
 *
 * @see Corp
 * @see JpaRepository
 * @see Optional
 * @see List
 * @see Query
 * @see Modifying
 * @see Transactional
 * @see Param
 * @see jakarta.transaction.Transactional
 * @see org.springframework.data.jpa.repository.Modifying
 * @see org.springframework.data.jpa.repository.Query
 * @see org.springframework.data.repository.query.Param
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
public interface CorpRepository extends JpaRepository<Corp, String> {

    /**
     * Retrieves the lifetime of a corporation by order ID and shop ID.
     *
     * @param orderId the order ID
     * @param shopId the shop ID
     * @return the lifetime of the corporation
     */
    @Query("SELECT c.lifeTime FROM Corp c JOIN c.shops s JOIN s.orders o WHERE o.orderId = :orderId AND s.shopId = :shopId")
    Integer findLifetimeByOrderIdAndShopId(@Param("orderId") String orderId, @Param("shopId") String shopId);

    /**
     * Retrieves a corporation by its ID using a native query.
     *
     * @param idCorp the corporation ID
     * @return an {@link Optional} containing the corporation, if found
     */
    @Query(value = "SELECT * FROM corp WHERE corp_id = ?1", nativeQuery = true)
    Optional<Corp> getCorpByCorpId(String idCorp);

    /**
     * Retrieves all corporations using a native query.
     *
     * @return an {@link Optional} containing a list of all corporations
     */
    @Query(value = "SELECT * FROM corp", nativeQuery = true)
    Optional<List<Corp>> getAllCorps();

    /**
     * Updates the login of a corporation.
     *
     * @param corpId the corporation ID
     * @param login the new login
     */
    @Modifying
    @Transactional
    @Query("UPDATE Corp c SET c.login = :login WHERE c.corpId = :corpId")
    void updateLogin(@Param("corpId") String corpId, @Param("login") String login);

    /**
     * Updates the password of a corporation.
     *
     * @param corpId the corporation ID
     * @param password the new password
     */
    @Modifying
    @Transactional
    @Query("UPDATE Corp c SET c.password = :password WHERE c.corpId = :corpId")
    void updatePassword(@Param("corpId") String corpId, @Param("password") String password);

    /**
     * Updates the name of a corporation.
     *
     * @param corpId the corporation ID
     * @param corpName the new corporation name
     */
    @Modifying
    @Transactional
    @Query("UPDATE Corp c SET c.corpName = :corpName WHERE c.corpId = :corpId")
    void updateCorpName(@Param("corpId") String corpId, @Param("corpName") String corpName);

    /**
     * Updates the lifetime of a corporation.
     *
     * @param corpId the corporation ID
     * @param lifeTime the new lifetime
     */
    @Modifying
    @Transactional
    @Query("UPDATE Corp c SET c.lifeTime = :lifeTime WHERE c.corpId = :corpId")
    void updateLifeTime(@Param("corpId") String corpId, @Param("lifeTime") Integer lifeTime);
}
