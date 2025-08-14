package kls.tgb.mapper;

import kls.tgb.dao.entities.UserEntity;
import kls.tgb.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Mapping(target = "selfUserName", source = "selfUserName")
    public abstract UserDto fromUserEntityToUserDto(UserEntity userEntity);

}
