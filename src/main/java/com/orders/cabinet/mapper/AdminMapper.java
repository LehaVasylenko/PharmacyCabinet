package com.orders.cabinet.mapper;

import com.orders.cabinet.model.db.Admin;
import com.orders.cabinet.model.db.dto.AdminDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminMapper {
    AdminMapper INSTANCE = Mappers.getMapper(AdminMapper.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "role", target = "role")
    Admin toModel(AdminDTO adminDTO);


    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "role", target = "role")
    AdminDTO toDto(Admin admin);
}
