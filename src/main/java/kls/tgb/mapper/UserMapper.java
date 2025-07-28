package kls.tgb.mapper;

import kls.tgb.dao.entities.ConstructionProjectEntity;
import kls.tgb.dao.entities.UserEntity;
import kls.tgb.dto.ConstructionProjectDto;
import kls.tgb.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.telegram.telegrambots.meta.api.objects.User;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Mapping(target = "telegramId", source = "id")
    @Mapping(target = "username", source = "userName")
    public abstract UserDto fromTgUserToUserDto(User telegramUser);

    @Mapping(target = "selfUserName", source = "selfUserName")
    public abstract UserDto fromUserEntityToUserDto(UserEntity userEntity);

}
