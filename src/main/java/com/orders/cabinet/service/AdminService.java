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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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


    @Async
    public CompletableFuture<Void> saveCorp(List<CorpDTO> corpDTO) {
        try {
            for (int i = 0; i < corpDTO.size(); i++) {
                Optional<Corp> exceptionCorp = corpRepository.getCorpByCorpId(corpDTO.get(i).getCorpId());
                if (exceptionCorp.isPresent())
                    throw new SQLException(corpDTO.get(i).getCorpId() + " already exists in base!");
                corpRepository.save(CorpMapper.INSTANCE.toModel(corpDTO.get(i)));
            }
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    public CompletableFuture<CorpDTO> getCorpInfoById (String corpId) {
        return CompletableFuture
                .completedFuture(CorpMapper
                        .INSTANCE
                        .toDto(corpRepository
                                .getCorpByCorpId(corpId)
                                .orElseThrow(() -> new NoSuchShopException("No corp with " + corpId + " was found in DB!"))
                        )
                );
    }

    @Async
    public CompletableFuture<List<CorpDTO>> getAllCorpInfo () {
        return CompletableFuture
                .completedFuture(corpRepository
                .getAllCorps().orElseThrow(() -> new NoSuchShopException("No any corp in DB"))
                .stream()
                .map(CorpMapper.INSTANCE::toDto)
                .collect(Collectors.toList()));
    }

    @Async
    public CompletableFuture<String> deleteCorp(String corpId) {
        try {
            corpRepository.deleteById(corpId);
            return CompletableFuture.completedFuture("OK");
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

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

    @Async
    public CompletableFuture<String> editShopById(String shopId, String password) {
        try {
            shopRepository.updatePassword(shopId, encoder.encode(password));
            return CompletableFuture.completedFuture("OK");
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    @Transactional
    public CompletableFuture<List<ShopInfoCacheDTO>> saveShop(List<AddShopDTO> addShopDTO) {
        boolean flag = false;
        List<ShopInfoCacheDTO> result = new ArrayList<>();
        try {
            for (int i = 0; i < addShopDTO.size(); i++) {
                Optional<Shops> exceptionShop = shopRepository.getShopByShopId(addShopDTO.get(i).getShopId());
                if (exceptionShop.isPresent())
                    throw new SQLException(addShopDTO.get(i).getShopId() + " already exists in base!");
                ShopInfoCacheDTO shopInfoCacheDTO = getShopInfo(addShopDTO.get(i).getShopId());
                if (shopInfoCacheDTO != null) {
                    Shops shop = shopMapper.toModel(ShopsDTO
                            .builder()
                            .shopId(addShopDTO.get(i).getShopId())
                            .password(addShopDTO.get(i).getPassword())
                            .corpId(addShopDTO.get(i).getCorpId())
                            .build());
                    shop.setRole(Role.SHOP);
                    shopRepository.save(shop);
                    result.add(shopInfoCacheDTO);
                } else throw new NoSuchShopException("No shop " + addShopDTO.get(i).getShopId() + " in DB Geoapteki!");
            }
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    public CompletableFuture<?> saveAdmin(AdminDTO adminDTO) {
        try {
            if (!adminRepository.existsByUsername(adminDTO.getUsername())) {
                String rawPassword = adminDTO.getPassword();
                adminDTO.setPassword(encoder.encode(rawPassword));
                adminRepository.save(AdminMapper.INSTANCE.toModel(adminDTO));
                return CompletableFuture.completedFuture("OK");
            } else return CompletableFuture.failedFuture(new SQLException("Admin with such username already exists!"));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    public CompletableFuture<String> deleteShop (String shopId) {
        try {
            shopRepository.deleteById(shopId);
            return CompletableFuture.completedFuture("OK");
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    public CompletableFuture<List<ShopsDTO>> getAllLoggedShops() {
        return CompletableFuture
                .completedFuture(shopRepository.findAll()
                .stream()
                .map(shopMapper::toDto)
                .collect(Collectors.toList()));
    }

    @Async
    public CompletableFuture<ShopsDTO> getShopById(String shopId) {
        ShopsDTO dto = shopMapper.toDto(shopRepository.getShopByShopId(shopId)
                        .orElseThrow(() -> new NoSuchShopException("No shop with ID: " + shopId + " was found!")));
        dto.setPassword("******");

        return CompletableFuture
                .completedFuture(dto);
    }

    private ShopInfoCacheDTO getShopInfo(String shopId) {
        Optional<ShopInfoCache> shopInfoCache = shopInfoCacheRepository.findById(shopId);
        if (shopInfoCache.isPresent()) return ShopInfoCachRepositoryMapper.INSTANCE.toDto(shopInfoCache.get());
        else {
            ShopInfoCacheDTO response = getShopInfoCacheDTOResponseEntity(shopId);

            shopInfoCacheRepository.save(ShopInfoCachRepositoryMapper.INSTANCE.toModel(response));
            return response;
        }
    }

    private ShopInfoCacheDTO getShopInfoCacheDTOResponseEntity(String shopId) {
        String url = "https://api.apteki.ua/get_shop/" + shopId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<ShopInfoCacheDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                ShopInfoCacheDTO.class
        );
        return response.getBody();
    }
}
