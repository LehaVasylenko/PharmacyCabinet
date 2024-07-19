package com.orders.cabinet.mapper;

import com.orders.cabinet.model.db.ShopInfoCache;
import com.orders.cabinet.model.db.dto.ShopInfoCacheDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between {@link ShopInfoCache} and {@link ShopInfoCacheDTO}.
 *
 * <p>This interface uses MapStruct to map fields between the {@link ShopInfoCache} entity and the
 * {@link ShopInfoCacheDTO} data transfer object. This allows for seamless conversion between
 * database entities and DTOs used in application layers.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Mapper
public interface ShopInfoCachRepositoryMapper {
    /**
     * Instance of the mapper.
     */
    ShopInfoCachRepositoryMapper INSTANCE = Mappers.getMapper(ShopInfoCachRepositoryMapper.class);

    /**
     * Maps a {@link ShopInfoCacheDTO} to a {@link ShopInfoCache} entity.
     *
     * <p>This method maps each field from the {@link ShopInfoCacheDTO} to the corresponding field
     * in the {@link ShopInfoCache} entity. This is useful for converting DTOs to entities for
     * persistence in the database.</p>
     *
     * @param shopInfoCacheDTO the {@link ShopInfoCacheDTO} to be converted
     * @return the corresponding {@link ShopInfoCache} entity
     */
    @Mapping(source = "shopId", target = "shopId")
    @Mapping(source = "corpId", target = "corpId")
    @Mapping(source = "corpName", target = "corpName")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "mark", target = "mark")
    @Mapping(source = "area", target = "area")
    @Mapping(source = "city", target = "city")
    @Mapping(source = "street", target = "street")
    @Mapping(source = "update", target = "update")
    @Mapping(source = "openHours", target = "openHours")
    ShopInfoCache toModel(ShopInfoCacheDTO shopInfoCacheDTO);

    /**
     * Maps a {@link ShopInfoCache} entity to a {@link ShopInfoCacheDTO}.
     *
     * <p>This method maps each field from the {@link ShopInfoCache} entity to the corresponding field
     * in the {@link ShopInfoCacheDTO}. This is useful for converting entities to DTOs for use in
     * application layers or APIs.</p>
     *
     * @param shopInfoCache the {@link ShopInfoCache} entity to be converted
     * @return the corresponding {@link ShopInfoCacheDTO}
     */
    @Mapping(source = "shopId", target = "shopId")
    @Mapping(source = "corpId", target = "corpId")
    @Mapping(source = "corpName", target = "corpName")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "mark", target = "mark")
    @Mapping(source = "area", target = "area")
    @Mapping(source = "city", target = "city")
    @Mapping(source = "street", target = "street")
    @Mapping(source = "update", target = "update")
    @Mapping(source = "openHours", target = "openHours")
    ShopInfoCacheDTO toDto(ShopInfoCache shopInfoCache);
}
