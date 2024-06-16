package com.jacdl.service.mapper;

import com.jacdl.domain.Delivery;
import com.jacdl.domain.Provider;
import com.jacdl.domain.User;
import com.jacdl.service.dto.DeliveryDTO;
import com.jacdl.service.dto.ProviderDTO;
import com.jacdl.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Delivery} and its DTO {@link DeliveryDTO}.
 */
@Mapper(componentModel = "spring")
public interface DeliveryMapper extends EntityMapper<DeliveryDTO, Delivery> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "providers", source = "providers", qualifiedByName = "providerIdSet")
    DeliveryDTO toDto(Delivery s);

    @Mapping(target = "providers", ignore = true)
    @Mapping(target = "removeProvider", ignore = true)
    Delivery toEntity(DeliveryDTO deliveryDTO);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("providerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProviderDTO toDtoProviderId(Provider provider);

    @Named("providerIdSet")
    default Set<ProviderDTO> toDtoProviderIdSet(Set<Provider> provider) {
        return provider.stream().map(this::toDtoProviderId).collect(Collectors.toSet());
    }
}
