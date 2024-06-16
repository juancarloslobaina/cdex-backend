package com.jacdl.service.mapper;

import com.jacdl.domain.Client;
import com.jacdl.domain.Provider;
import com.jacdl.domain.User;
import com.jacdl.service.dto.ClientDTO;
import com.jacdl.service.dto.ProviderDTO;
import com.jacdl.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Client} and its DTO {@link ClientDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClientMapper extends EntityMapper<ClientDTO, Client> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "providers", source = "providers", qualifiedByName = "providerIdSet")
    ClientDTO toDto(Client s);

    @Mapping(target = "providers", ignore = true)
    @Mapping(target = "removeProvider", ignore = true)
    Client toEntity(ClientDTO clientDTO);

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
