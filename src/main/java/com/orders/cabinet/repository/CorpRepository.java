package com.orders.cabinet.repository;

import com.orders.cabinet.model.db.Corp;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CorpRepository extends JpaRepository<Corp, String> {
    @Query(value = "SELECT * FROM corp WHERE corp_id = ?1", nativeQuery = true)
    Optional<Corp> getCorpByCorpId(String idCorp);

    @Query(value = "SELECT * FROM corp", nativeQuery = true)
    Optional<List<Corp>> getAllCorps();

    @Modifying
    @Transactional
    @Query("UPDATE Corp c SET c.login = :login WHERE c.corpId = :corpId")
    void updateLogin(@Param("corpId") String corpId, @Param("login") String login);

    @Modifying
    @Transactional
    @Query("UPDATE Corp c SET c.password = :password WHERE c.corpId = :corpId")
    void updatePassword(@Param("corpId") String corpId, @Param("password") String password);

    @Modifying
    @Transactional
    @Query("UPDATE Corp c SET c.corpName = :corpName WHERE c.corpId = :corpId")
    void updateCorpName(@Param("corpId") String corpId, @Param("corpName") String corpName);

    @Modifying
    @Transactional
    @Query("UPDATE Corp c SET c.lifeTime = :lifeTime WHERE c.corpId = :corpId")
    void updateLifeTime(@Param("corpId") String corpId, @Param("lifeTime") Integer lifeTime);
}
