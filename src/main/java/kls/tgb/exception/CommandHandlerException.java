package kls.tgb.exception;

import lombok.Getter;

@Getter
public class CommandHandlerException extends RuntimeException {

    private final Long chatId;

    public CommandHandlerException(String message, Long chatId) {
        super(message);
        this.chatId = chatId;
    }
}
