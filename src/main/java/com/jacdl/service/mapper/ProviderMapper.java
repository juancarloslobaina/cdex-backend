package com.jacdl.service.mapper;

import com.jacdl.domain.Client;
import com.jacdl.domain.Delivery;
import com.jacdl.domain.Provider;
import com.jacdl.domain.User;
import com.jacdl.service.dto.ClientDTO;
import com.jacdl.service.dto.DeliveryDTO;
import com.jacdl.service.dto.ProviderDTO;
import com.jacdl.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Provider} and its DTO {@link ProviderDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProviderMapper extends EntityMapper<ProviderDTO, Provider> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "clients", source = "clients", qualifiedByName = "clientIdSet")
    @Mapping(target = "deliveries", source = "deliveries", qualifiedByName = "deliveryIdSet")
    ProviderDTO toDto(Provider s);

    @Mapping(target = "removeClient", ignore = true)
    @Mapping(target = "removeDelivery", ignore = true)
    Provider toEntity(ProviderDTO providerDTO);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("clientId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClientDTO toDtoClientId(Client client);

    @Named("clientIdSet")
    default Set<ClientDTO> toDtoClientIdSet(Set<Client> client) {
        return client.stream().map(this::toDtoClientId).collect(Collectors.toSet());
    }

    @Named("deliveryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DeliveryDTO toDtoDeliveryId(Delivery delivery);

    @Named("deliveryIdSet")
    default Set<DeliveryDTO> toDtoDeliveryIdSet(Set<Delivery> delivery) {
        return delivery.stream().map(this::toDtoDeliveryId).collect(Collectors.toSet());
    }
}
