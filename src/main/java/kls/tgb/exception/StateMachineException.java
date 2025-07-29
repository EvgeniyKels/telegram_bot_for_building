package kls.tgb.exception;

import kls.tgb.dto.sm.State;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class StateMachineException extends RuntimeException {
    private final Long chatId;
    private final State failedState;
    private final Long userId;

    public StateMachineException(Long chatId, State state, @NonNull Long userTgId) {
        this.chatId = chatId;
        this.failedState = state;
        this.userId = userTgId;
    }
}
