package kls.tgb.service;

import kls.tgb.dto.sm.MessageButtonHolder;
import kls.tgb.dto.sm.StartCommandState;

public interface StateMachine <T> {

    MessageButtonHolder handleStartCommandStates(Long userTgId, T dto);

}
