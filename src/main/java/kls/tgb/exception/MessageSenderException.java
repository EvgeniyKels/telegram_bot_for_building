package kls.tgb.exception;

import lombok.Getter;

@Getter
public class MessageSenderException extends RuntimeException {

    private final Long chatId;

    public MessageSenderException(String message, Long chatId) {
        super(message);
        this.chatId = chatId;
    }
}
