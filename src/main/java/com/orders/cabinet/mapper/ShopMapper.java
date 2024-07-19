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
/**
 * Mapper class for converting between {@link Shops} and {@link ShopsDTO}.
 *
 * <p>This class provides methods to map fields between the {@link Shops} entity and the
 * {@link ShopsDTO} data transfer object. It handles the conversion of shop information, including
 * password encoding and lookup of related corporate entities.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@RequiredArgsConstructor
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShopMapper {

    CorpRepository repository;
    PasswordEncoder encoder;

    /**
     * Converts a {@link ShopsDTO} to a {@link Shops} entity.
     *
     * <p>This method retrieves the {@link Corp} entity associated with the given {@link ShopsDTO}
     * by its corporate ID. It then creates and returns a {@link Shops} entity with the appropriate
     * fields, including encoding the password using the provided {@link PasswordEncoder}.</p>
     *
     * @param shopsDto the {@link ShopsDTO} to be converted
     * @return the corresponding {@link Shops} entity
     * @throws NoSuchShopException if no {@link Corp} entity with the given ID is found
     */
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

    /**
     * Converts a {@link Shops} entity to a {@link ShopsDTO}.
     *
     * <p>This method creates and returns a {@link ShopsDTO} with the fields from the provided
     * {@link Shops} entity. The corporate ID is retrieved from the associated {@link Corp} entity.</p>
     *
     * @param shops the {@link Shops} entity to be converted
     * @return the corresponding {@link ShopsDTO}
     */
    public ShopsDTO toDto(Shops shops) {
        return ShopsDTO.builder()
                .shopId(shops.getShopId())
                .password(shops.getPassword())
                .corpId(shops.getCorp().getCorpId())
                .build();
    }
}
