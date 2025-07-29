package kls.tgb.service;

import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.MessageButtonHolder;
import kls.tgb.dto.sm.State;

public interface StateMachine {
    MessageButtonHolder handleStartCommandStates(Long userTgId, UserDto userDto);

    State getUserState(Long telegramId);

    void removeUserState(Long telegramId);
}
