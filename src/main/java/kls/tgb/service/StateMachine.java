package kls.tgb.service;

import kls.tgb.dto.sm.MessageButtonHolder;
import kls.tgb.dto.sm.StartCommandState;

public interface StateMachine <T, S> {

    MessageButtonHolder handleStartCommandStates(Long userTgId, T dto);

    S getUserState(Long telegramId);

    void removeUserState(Long telegramId);
}
