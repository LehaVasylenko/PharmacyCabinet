package com.orders.cabinet.service;

import com.orders.cabinet.exception.NoSuchShopException;
import com.orders.cabinet.mapper.CorpMapper;
import com.orders.cabinet.mapper.ShopMapper;
import com.orders.cabinet.model.Role;
import com.orders.cabinet.model.db.Corp;
import com.orders.cabinet.model.db.Shops;
import com.orders.cabinet.model.db.dto.AddShopDTO;
import com.orders.cabinet.model.db.dto.CorpDTO;
import com.orders.cabinet.model.db.dto.ShopsDTO;
import com.orders.cabinet.repository.CorpRepository;
import com.orders.cabinet.repository.ShopRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
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
    ShopMapper shopMapper;


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
    public void deleteCorp(String corpId) {

        shopRepository.deleteByCorpId(corpId);
        corpRepository.deleteById(corpId);
    }

    @Async
    public void editCorpById(String corpId, CorpDTO corpDto) {
        if (corpDto.getLogin() != null) corpRepository.updateLogin(corpId, corpDto.getLogin());
        if (corpDto.getPassword() != null) corpRepository.updatePassword(corpId, corpDto.getPassword());
        if (corpDto.getCorpName() != null) corpRepository.updateCorpName(corpId, corpDto.getCorpName());
        if (corpDto.getLifeTime() != null) corpRepository.updateLifeTime(corpId, corpDto.getLifeTime());
    }

    @Async
    public void editShopById(String shopId, String password) {
        shopRepository.updatePassword(shopId, password);
    }

    @Async
    public CompletableFuture<Void> saveShop(List<AddShopDTO> addShopDTO) throws SQLException {
        try {
            for (int i = 0; i < addShopDTO.size(); i++) {
                Optional<Shops> exceptionShop = shopRepository.getShopByShopId(addShopDTO.get(i).getShopId());
                if (exceptionShop.isPresent())
                    throw new SQLException(addShopDTO.get(i).getShopId() + " already exists in base!");
                Shops shop = shopMapper.toModel(ShopsDTO
                        .builder()
                        .shopId(addShopDTO.get(i).getShopId())
                        .password(addShopDTO.get(i).getPassword())
                        .corpId(addShopDTO.get(i).getCorpId())
                        .build());
                shop.setRole(Role.SHOP);
                shopRepository.save(shop);
            }
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    public CompletableFuture<Void> saveAdmin(ShopsDTO shopsDTO) {
        try {
            Optional<Shops> exceptionShop = shopRepository.getShopByShopId(shopsDTO.getShopId());
            if (exceptionShop.isPresent()) throw new SQLException(shopsDTO.getShopId() + " already exists in base!");
            Shops shop = shopMapper.toModel(shopsDTO);
            Corp corp = corpRepository
                    .getCorpByCorpId(shopsDTO.getCorpId())
                    .orElseThrow(() -> new NoSuchShopException("Add " + shopsDTO.getCorpId() + " to DB at first!"));

            shop.setRole(Role.ADMIN);
            shop.setCorp(corp);

            shopRepository.save(shop);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    public void deleteShop (String shopId) {
        shopRepository.deleteById(shopId);
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

        return CompletableFuture
                .completedFuture(shopMapper
                .toDto(shopRepository
                        .getShopByShopId(shopId)
                        .orElseThrow(() -> new NoSuchShopException("No shop with ID: " + shopId + " was found!"))
                ));
    }
}
