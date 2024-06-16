package com.jacdl.service.mapper;

import com.jacdl.domain.Product;
import com.jacdl.domain.Provider;
import com.jacdl.service.dto.ProductDTO;
import com.jacdl.service.dto.ProviderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "provider", source = "provider", qualifiedByName = "providerId")
    ProductDTO toDto(Product s);

    @Named("providerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProviderDTO toDtoProviderId(Provider provider);
}
