package com.orders.cabinet.mapper;

import com.orders.cabinet.model.db.Corp;
import com.orders.cabinet.model.db.dto.CorpDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
/**
 * Mapper interface for converting between {@link Corp} and {@link CorpDTO}.
 *
 * <p>This interface defines methods for mapping properties between the {@link Corp} entity
 * and the {@link CorpDTO} data transfer object. The mappings are handled by MapStruct, a
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
public interface CorpMapper {
    /**
     * Singleton instance of {@link CorpMapper}.
     */
    CorpMapper INSTANCE = Mappers.getMapper(CorpMapper.class);

    /**
     * Converts a {@link CorpDTO} to a {@link Corp} entity.
     *
     * <p>This method maps the fields from the {@link CorpDTO} object to the {@link Corp}
     * entity. The mapping is performed based on the specified source and target fields.</p>
     *
     * @param corpDTO the {@link CorpDTO} object to be converted
     * @return the corresponding {@link Corp} entity
     */
    @Mapping(source = "corpId", target = "corpId")
    @Mapping(source = "login", target = "login")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "corpName", target = "corpName")
    @Mapping(source = "lifeTime", target = "lifeTime")
    Corp toModel(CorpDTO corpDTO);

    /**
     * Converts a {@link Corp} entity to a {@link CorpDTO}.
     *
     * <p>This method maps the fields from the {@link Corp} entity to the {@link CorpDTO}
     * object. The mapping is performed based on the specified source and target fields.</p>
     *
     * @param corp the {@link Corp} entity to be converted
     * @return the corresponding {@link CorpDTO} object
     */
    @Mapping(source = "corpId", target = "corpId")
    @Mapping(source = "login", target = "login")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "corpName", target = "corpName")
    @Mapping(source = "lifeTime", target = "lifeTime")
    CorpDTO toDto(Corp corp);
}
