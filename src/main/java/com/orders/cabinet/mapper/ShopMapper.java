package com.orders.cabinet.mapper;

import com.orders.cabinet.exception.NoSuchShopException;
import com.orders.cabinet.model.Role;
import com.orders.cabinet.model.db.Corp;
import com.orders.cabinet.model.db.Shops;
import com.orders.cabinet.model.db.dto.ShopsDTO;
import com.orders.cabinet.repository.CorpRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShopMapper {

    CorpRepository repository;
    PasswordEncoder encoder;
    public Shops toModel(ShopsDTO shopsDto) {
        Corp corp = repository
                .getCorpByCorpId(shopsDto.getCorpId())
                .orElseThrow(() -> new NoSuchShopException("No corp with ID: " + shopsDto.getCorpId() + " were found in DB!"));
        return Shops.builder()
                .shopId(shopsDto.getShopId())
                .corp(corp)
                .role(Role.SHOP)
                .password(encoder.encode(shopsDto.getPassword()))
                .build();
    }

    public ShopsDTO toDto(Shops shops) {
        return ShopsDTO.builder()
                .shopId(shops.getShopId())
                .password(shops.getPassword())
                .corpId(shops.getCorp().getCorpId())
                .build();
    }
}
