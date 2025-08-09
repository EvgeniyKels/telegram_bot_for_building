package kls.tgb.service;

import kls.tgb.dto.TgUserChatDto;
import kls.tgb.dto.sm.MessageButtonHolder;

public interface StateMachine {

    MessageButtonHolder handleCommandStates(TgUserChatDto dto);

}
