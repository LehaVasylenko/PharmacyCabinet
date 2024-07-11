package com.orders.cabinet.mapper;

import com.orders.cabinet.model.db.Admin;
import com.orders.cabinet.model.db.ShopInfoCache;
import com.orders.cabinet.model.db.dto.AdminDTO;
import com.orders.cabinet.model.db.dto.ShopInfoCacheDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShopInfoCachRepositoryMapper {
    ShopInfoCachRepositoryMapper INSTANCE = Mappers.getMapper(ShopInfoCachRepositoryMapper.class);

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
