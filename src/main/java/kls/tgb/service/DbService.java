package kls.tgb.service;

import kls.tgb.dto.UserDto;

public interface DbService {
    UserDto registerOrUpdateUser(UserDto userDto);
}
