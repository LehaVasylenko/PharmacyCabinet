package com.orders.cabinet.service;

import com.orders.cabinet.exception.NoSuchShopException;
import com.orders.cabinet.mapper.AdminMapper;
import com.orders.cabinet.mapper.CorpMapper;
import com.orders.cabinet.mapper.ShopInfoCachRepositoryMapper;
import com.orders.cabinet.mapper.ShopMapper;
import com.orders.cabinet.model.Role;
import com.orders.cabinet.model.db.Corp;
import com.orders.cabinet.model.db.ShopInfoCache;
import com.orders.cabinet.model.db.Shops;
import com.orders.cabinet.model.db.dto.*;
import com.orders.cabinet.model.db.order.OrderDb;
import com.orders.cabinet.repository.AdminRepository;
import com.orders.cabinet.repository.CorpRepository;
import com.orders.cabinet.repository.ShopInfoCacheRepository;
import com.orders.cabinet.repository.ShopRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
/**
 * Service for handling admin-related operations.
 *
 * <p>This service provides asynchronous methods for managing corporations, shops,
 * and administrators, including saving, retrieving, updating, and deleting entities.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminService {

    CorpRepository corpRepository;
    ShopRepository shopRepository;
    AdminRepository adminRepository;
    ShopInfoCacheRepository shopInfoCacheRepository;
    ShopMapper shopMapper;
    PasswordEncoder encoder;
    RestTemplate restTemplate;

    /**
     * Saves a list of corporations.
     *
     * @param corpDTO the list of CorpDTOs to save
     * @return a CompletableFuture representing the completion of the operation
     */
    @Async
    public CompletableFuture<Void> saveCorp(List<CorpDTO> corpDTO) {
        try {
            for (int i = 0; i < corpDTO.size(); i++) {
                Optional<Corp> exceptionCorp = corpRepository.getCorpByCorpId(corpDTO.get(i).getCorpId());
                if (exceptionCorp.isPresent())
                    throw new SQLException(new StringBuilder().append(corpDTO.get(i).getCorpId()).append(" already exists in base!").toString());
                corpRepository.save(CorpMapper.INSTANCE.toModel(corpDTO.get(i)));
            }
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Retrieves corporation information by ID.
     *
     * @param corpId the ID of the corporation
     * @return a CompletableFuture containing the CorpDTO
     */
    @Async
    public CompletableFuture<CorpDTO> getCorpInfoById (String corpId) {
        return CompletableFuture
                .completedFuture(CorpMapper
                        .INSTANCE
                        .toDto(corpRepository
                                .getCorpByCorpId(corpId)
                                .orElseThrow(() -> new NoSuchShopException(new StringBuilder().append("No corp with ").append(corpId).append(" was found in DB!")))
                        )
                );
    }

    /**
     * Retrieves all corporation information.
     *
     * @return a CompletableFuture containing a list of CorpDTOs
     */
    @Async
    public CompletableFuture<List<CorpDTO>> getAllCorpInfo () {
        return CompletableFuture
                .completedFuture(corpRepository
                .getAllCorps().orElseThrow(() -> new NoSuchShopException("No any corp in DB"))
                .stream()
                .map(CorpMapper.INSTANCE::toDto)
                .collect(Collectors.toList()));
    }

    /**
     * Deletes a corporation by ID.
     *
     * @param corpId the ID of the corporation to delete
     * @return a CompletableFuture containing the result of the operation
     */
    @Async
    public CompletableFuture<String> deleteCorp(String corpId) {
        try {
            corpRepository.deleteById(corpId);
            return CompletableFuture.completedFuture("OK");
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Edits a corporation by ID.
     *
     * @param corpId the ID of the corporation to edit
     * @param corpDto the CorpDTO containing the new information
     * @return a CompletableFuture containing the result of the operation
     */
    @Async
    public CompletableFuture<String> editCorpById(String corpId, CorpDTO corpDto) {
        try {
            if (corpDto.getLogin() != null) corpRepository.updateLogin(corpId, corpDto.getLogin());
            if (corpDto.getPassword() != null) corpRepository.updatePassword(corpId, corpDto.getPassword());
            if (corpDto.getCorpName() != null) corpRepository.updateCorpName(corpId, corpDto.getCorpName());
            if (corpDto.getLifeTime() != null) corpRepository.updateLifeTime(corpId, corpDto.getLifeTime());
            return CompletableFuture.completedFuture(corpDto.toString());
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Edits a shop's password by ID.
     *
     * @param shopId the ID of the shop
     * @param password the new password
     * @return a CompletableFuture containing the result of the operation
     */
    @Async
    public CompletableFuture<String> editShopById(String shopId, String password) {
        try {
            shopRepository.updatePassword(shopId, encoder.encode(password));
            return CompletableFuture.completedFuture("OK");
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Saves a list of shops.
     *
     * @param addShopDTO the list of AddShopDTOs to save
     * @return a CompletableFuture containing a list of ShopInfoCacheDTOs
     */
    @Async
    @Transactional
    public CompletableFuture<List<ShopInfoCacheDTO>> saveShop(List<AddShopDTO> addShopDTO) {
        log.info(addShopDTO
                .stream()
                .map(AddShopDTO::toString)
                .collect(Collectors.joining("\n")));
        boolean flag = false;
        List<ShopInfoCacheDTO> result = new ArrayList<>();
        List<Shops> shopsResult = new ArrayList<>();
        try {
            for (int i = 0; i < addShopDTO.size(); i++) {
                Optional<Shops> exceptionShop = shopRepository.getShopByShopId(addShopDTO.get(i).getShopId());
                if (exceptionShop.isPresent())
                    throw new SQLException(addShopDTO.get(i).getShopId() + " already exists in base!");
                ShopInfoCacheDTO shopInfoCacheDTO = getShopInfo(addShopDTO.get(i).getShopId());
                Shops shop = shopMapper.toModel(ShopsDTO
                        .builder()
                        .shopId(addShopDTO.get(i).getShopId())
                        .password(addShopDTO.get(i).getPassword())
                        .corpId(addShopDTO.get(i).getCorpId())
                        .loggedIn(false)
                        .build());
                shop.setRole(Role.SHOP);
                shopsResult.add(shop);
                result.add(shopInfoCacheDTO);

            }
            shopRepository.saveAll(shopsResult
                    .stream()
                    .filter(item -> item.getRole() != null)
                    .toList());
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Saves an admin.
     *
     * @param adminDTO the AdminDTO to save
     * @return a CompletableFuture representing the completion of the operation
     */
    @Async
    public CompletableFuture<?> saveAdmin(AdminDTO adminDTO) {
        try {
            if (!adminRepository.existsByUsername(adminDTO.getUsername())) {
                String rawPassword = adminDTO.getPassword();
                adminDTO.setPassword(encoder.encode(rawPassword));
                adminDTO.setRole(Role.ADMIN);
                adminRepository.save(AdminMapper.INSTANCE.toModel(adminDTO));
                return CompletableFuture.completedFuture("OK");
            } else return CompletableFuture.failedFuture(new SQLException("Admin with such username already exists!"));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Deletes a shop by ID.
     *
     * @param shopId the ID of the shop to delete
     * @return a CompletableFuture containing the result of the operation
     */
    @Async
    public CompletableFuture<String> deleteShop (String shopId) {
        try {
            shopRepository.deleteById(shopId);
            return CompletableFuture.completedFuture("OK");
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Retrieves all logged shops. Not used
     *
     * @return a CompletableFuture containing a list of ShopsDTOs
     */
    @Async
    public CompletableFuture<List<ShopsDTO>> getAllLoggedShops() {
        return CompletableFuture
                .completedFuture(shopRepository.findAll()
                .stream()
                .map(shopMapper::toDto)
                .collect(Collectors.toList()));
    }

    /**
     * Retrieves shop information by ID.
     *
     * @param shopId the ID of the shop
     * @return a CompletableFuture containing the ShopsDTO
     */
    @Async
    public CompletableFuture<ShopsDTO> getShopById(String shopId) {
        ShopsDTO dto = shopMapper.toDto(shopRepository.getShopByShopId(shopId)
                        .orElseThrow(() -> new NoSuchShopException(new StringBuilder().append("No shop with ID: ").append(shopId).append(" was found!"))));
        dto.setPassword("******");

        return CompletableFuture
                .completedFuture(dto);
    }

    /**
     * Retrieves shop information.
     *
     * @param shopId the ID of the shop
     * @return the ShopInfoCacheDTO
     */
    private ShopInfoCacheDTO getShopInfo(String shopId) {
        Optional<ShopInfoCache> shopInfoCache = shopInfoCacheRepository.findById(shopId);
        if (shopInfoCache.isPresent()) return ShopInfoCachRepositoryMapper.INSTANCE.toDto(shopInfoCache.get());
        else {
            ShopInfoCacheDTO response = getShopInfoCacheDTOResponseEntity(shopId);
            if (response != null)
                shopInfoCacheRepository.save(ShopInfoCachRepositoryMapper.INSTANCE.toModel(response));
            else throw new NoSuchShopException(new StringBuilder("No shop ").append(shopId).append(" in DB Geoapteki!"));
            return response;
        }
    }

    /**
     * Retrieves shop information from an external API.
     *
     * @param shopId the ID of the shop
     * @return the ShopInfoCacheDTO from the external API
     */
    public ShopInfoCacheDTO getShopInfoCacheDTOResponseEntity(String shopId) {
        String url = new StringBuilder().append("https://api.apteki.ua/get_shop/").append(shopId).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<ShopInfoCacheDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                ShopInfoCacheDTO.class
        );
        log.info(response.toString());
        return response.getBody();
    }
}
