package kls.tgb.dao;

import kls.tgb.dto.UserDto;

public interface DbService {
    UserDto registerOrUpdateUser(UserDto userDto);
}
