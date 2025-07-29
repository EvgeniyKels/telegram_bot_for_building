package kls.tgb.telegram.userinputhandlers.commandhandlers;

import kls.tgb.mapper.UserMapper;
import kls.tgb.service.StateMachine;
import kls.tgb.service.StateMachineImpl;
import kls.tgb.telegram.MessageSender;
import kls.tgb.telegram.userinputhandlers.UserInputHandlerUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import static kls.tgb.util.StringConstants.*;

@Component
public class CommandReloadHandlerImpl implements CommandHandler{

    @Value("${telegram.handler.description.remove}")
    private String startHandlerDescription;

    private final UserInputHandlerUtils userInputHandlerUtils;
    private final StateMachine stateMachine;
    private final MessageSender messageSender;

    public CommandReloadHandlerImpl(UserInputHandlerUtils userInputHandlerUtils, StateMachine stateMachine, MessageSender messageSender) {
        this.userInputHandlerUtils = userInputHandlerUtils;
        this.stateMachine = stateMachine;
        this.messageSender = messageSender;
    }

    @Override
    public void handle(Message message) {
        final var prepareHandlerData = userInputHandlerUtils.prepareHandlerData(message);
        stateMachine.removeUserState(prepareHandlerData.userDto().getTelegramId());
        messageSender.sendMessage(String.valueOf(prepareHandlerData.chatId()), "удалено");
    }

    @Override
    public String getHandlerName() {
        return SLASH.concat(REMOVE);
    }

    @Override
    public String getHandlerDescription() {
        return startHandlerDescription;
    }
}
