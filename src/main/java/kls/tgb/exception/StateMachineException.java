package kls.tgb.exception;

import kls.tgb.dto.sm.StartCommandState;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class StateMachineException extends RuntimeException {
    private final Long chatId;
    private final String failedState;
    private final Long userId;

    public StateMachineException(Long chatId, String state, @NonNull Long userTgId) {
        this.chatId = chatId;
        this.failedState = state;
        this.userId = userTgId;
    }
}
