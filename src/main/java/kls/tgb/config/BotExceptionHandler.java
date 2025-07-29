package kls.tgb.config;

import kls.tgb.dto.sm.MessageButtonHolder;
import kls.tgb.exception.CommandHandlerException;
import kls.tgb.exception.MessageSenderException;
import kls.tgb.exception.StateMachineException;
import kls.tgb.telegram.MessageSender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class BotExceptionHandler {

    private final MessageSender messageSender;

    @ExceptionHandler(CommandHandlerException.class)
    public void handleCommandHandlerException(CommandHandlerException ex) {
        messageSender.sendMessage(ex.getChatId().toString(), "Произошла ошибка. Попробуйте позже.");
    }

    @ExceptionHandler(MessageSenderException.class)
    public void handleMessageSenderException(MessageSenderException ex) {
        messageSender.sendMessage(ex.getChatId().toString(), "Произошла ошибка. Попробуйте позже.");
    }

    @ExceptionHandler(StateMachineException.class)
    public void handleException(StateMachineException ex) {
        log.error("State machine failed for user {} at state {}", ex.getUserId(), ex.getFailedState());
        messageSender.sendMessage(String.valueOf(ex.getChatId()), "Произошла ошибка. Попробуйте позже.");
    }

}
