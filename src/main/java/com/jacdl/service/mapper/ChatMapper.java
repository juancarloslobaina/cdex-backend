package com.jacdl.service.mapper;

import com.jacdl.domain.Chat;
import com.jacdl.domain.User;
import com.jacdl.service.dto.ChatDTO;
import com.jacdl.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Chat} and its DTO {@link ChatDTO}.
 */
@Mapper(componentModel = "spring")
public interface ChatMapper extends EntityMapper<ChatDTO, Chat> {
    @Mapping(target = "from", source = "from", qualifiedByName = "userLogin")
    @Mapping(target = "to", source = "to", qualifiedByName = "userLogin")
    ChatDTO toDto(Chat s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
