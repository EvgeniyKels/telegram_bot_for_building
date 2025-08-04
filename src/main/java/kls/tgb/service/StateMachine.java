package kls.tgb.service;

import kls.tgb.dto.sm.MessageButtonHolder;

public interface StateMachine <T, S> {

    MessageButtonHolder handleCommandStates(Long userTgId, T dto);

    S getUserState(Long telegramId);

    void removeUserState(Long telegramId);
}
