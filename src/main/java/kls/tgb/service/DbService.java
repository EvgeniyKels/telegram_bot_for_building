package kls.tgb.service;

import kls.tgb.dto.StateDto;
import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.State;
import lombok.NonNull;

public interface DbService {
    UserDto registerOrUpdateUser(final Long telegramId, UserDto selfUserName);

    StateDto getStateByTgID(@NonNull Long id);

    void setState(Long telegramId, State state);
}
