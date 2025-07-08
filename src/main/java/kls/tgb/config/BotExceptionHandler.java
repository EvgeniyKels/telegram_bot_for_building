package kls.tgb.config;

import kls.tgb.exception.CommandHandlerException;
import kls.tgb.exception.MessageSenderException;
import kls.tgb.telegram.MessageSender;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@AllArgsConstructor
public class BotExceptionHandler {

    private final MessageSender messageSender;

    @ExceptionHandler(CommandHandlerException.class)
    public void handleCommandHandlerException(CommandHandlerException ex) {
        messageSender.sendMessage(ex.getChatId().toString(), "какая то ошибка"); //TODO
    }

    @ExceptionHandler(MessageSenderException.class)
    public void handleMessageSenderException(MessageSenderException ex) {
        messageSender.sendMessage(ex.getChatId().toString(), "какая то ошибка"); //TODO
    }

}
