package kls.tgb.telegram.userinputhandlers.commandhandlers;

import kls.tgb.mapper.UserMapper;
import kls.tgb.service.StateMachine;
import kls.tgb.service.StateMachineImpl;
import kls.tgb.telegram.MessageSender;
import kls.tgb.telegram.userinputhandlers.PreHandleDataHolder;
import kls.tgb.telegram.userinputhandlers.UserInputHandlerUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import static kls.tgb.util.StringConstants.*;

@Component
public class CommandStartHandlerImpl implements CommandHandler {

    private final MessageSender messageSender;
    private final StateMachine stateMachine;
    private final UserInputHandlerUtils userInputHandlerUtils;

    @Value("${telegram.handler.description.start}")
    private String startHandlerDescription;

    public CommandStartHandlerImpl(MessageSender messageSender, UserInputHandlerUtils userInputHandlerUtils, StateMachine stateMachine) {
        this.messageSender = messageSender;
        this.userInputHandlerUtils = userInputHandlerUtils;
        this.stateMachine = stateMachine;
    }

    @Override
    public void handle(Message message) {
        final var prepareHandlerData = userInputHandlerUtils.prepareHandlerData(message);
        final var registrationState = stateMachine.getUserState(prepareHandlerData.userDto().getTelegramId());
        prepareHandlerData.userDto().setState(registrationState);
        final var messageButtonHolder = stateMachine.handleStartCommandStates(prepareHandlerData.userDto().getTelegramId(), prepareHandlerData.userDto());

        messageSender.sendMessage(String.valueOf(prepareHandlerData.userDto().getChatId()), messageButtonHolder);
    }

    @Override
    public String getHandlerName() {
        return SLASH.concat(START);
    }

    @Override
    public String getHandlerDescription() {
        return startHandlerDescription;
    }
}
