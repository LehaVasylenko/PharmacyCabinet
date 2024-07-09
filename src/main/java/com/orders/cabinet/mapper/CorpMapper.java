package com.orders.cabinet.mapper;

import com.orders.cabinet.model.db.Corp;
import com.orders.cabinet.model.db.dto.CorpDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CorpMapper {
    CorpMapper INSTANCE = Mappers.getMapper(CorpMapper.class);

    @Mapping(source = "corpId", target = "corpId")
    @Mapping(source = "login", target = "login")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "corpName", target = "corpName")
    @Mapping(source = "lifeTime", target = "lifeTime")
    Corp toModel(CorpDTO corpDTO);


    @Mapping(source = "corpId", target = "corpId")
    @Mapping(source = "login", target = "login")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "corpName", target = "corpName")
    @Mapping(source = "lifeTime", target = "lifeTime")
    CorpDTO toDto(Corp corp);
}
