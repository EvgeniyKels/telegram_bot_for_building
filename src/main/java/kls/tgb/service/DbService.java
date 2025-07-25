package kls.tgb.service;

import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.RegistrationState;
import lombok.NonNull;

public interface DbService {
    UserDto registerOrUpdateUser(UserDto userDto);

    RegistrationState getStateByTgID(@NonNull Long id);

    void setState(Long telegramId, RegistrationState state);
}
