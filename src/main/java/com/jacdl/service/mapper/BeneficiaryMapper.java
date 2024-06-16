package com.jacdl.service.mapper;

import com.jacdl.domain.Beneficiary;
import com.jacdl.domain.User;
import com.jacdl.service.dto.BeneficiaryDTO;
import com.jacdl.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Beneficiary} and its DTO {@link BeneficiaryDTO}.
 */
@Mapper(componentModel = "spring")
public interface BeneficiaryMapper extends EntityMapper<BeneficiaryDTO, Beneficiary> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    BeneficiaryDTO toDto(Beneficiary s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
