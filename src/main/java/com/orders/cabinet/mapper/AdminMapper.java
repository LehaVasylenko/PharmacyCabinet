package com.orders.cabinet.mapper;

import com.orders.cabinet.model.db.Admin;
import com.orders.cabinet.model.db.dto.AdminDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between {@link Admin} and {@link AdminDTO}.
 *
 * <p>This interface defines methods for mapping properties between the {@link Admin} entity
 * and the {@link AdminDTO} data transfer object. The mappings are handled by MapStruct, a
 * code generation tool for bean mapping.</p>
 *
 * <p>It provides a single instance of the mapper through the {@link Mappers#getMapper(Class)}
 * method. This is a standard approach in MapStruct to create a singleton instance of the mapper.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Mapper
public interface AdminMapper {
    /**
     * Singleton instance of {@link AdminMapper}.
     */
    AdminMapper INSTANCE = Mappers.getMapper(AdminMapper.class);

    /**
     * Converts an {@link AdminDTO} to an {@link Admin} entity.
     *
     * <p>This method maps the fields from the {@link AdminDTO} object to the {@link Admin}
     * entity. The mapping is performed based on the specified source and target fields.</p>
     *
     * @param adminDTO the {@link AdminDTO} object to be converted
     * @return the corresponding {@link Admin} entity
     */
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "role", target = "role")
    Admin toModel(AdminDTO adminDTO);

    /**
     * Converts an {@link Admin} entity to an {@link AdminDTO}.
     *
     * <p>This method maps the fields from the {@link Admin} entity to the {@link AdminDTO}
     * object. The mapping is performed based on the specified source and target fields.</p>
     *
     * @param admin the {@link Admin} entity to be converted
     * @return the corresponding {@link AdminDTO} object
     */
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "role", target = "role")
    AdminDTO toDto(Admin admin);
}
