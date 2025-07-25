package kls.tgb.telegram.userinputhandlers.commandhandlers;

import kls.tgb.mapper.UserMapper;
import kls.tgb.service.StateMachineImpl;
import kls.tgb.telegram.MessageSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import static kls.tgb.util.StringConstants.*;

@Component
public class CommandStartHandlerImpl implements CommandHandler {

    private final MessageSender messageSender;
    private final StateMachineImpl stateMachine; //TODO поменяй на интерфейс и вынеси в абстрактный класс
    private final UserMapper userMapper;


    @Value("${telegram.handler.description.start}")
    private String startHandlerDescription;

    public CommandStartHandlerImpl(MessageSender messageSender, UserMapper userMapper, StateMachineImpl stateMachine) {
        this.messageSender = messageSender;
        this.userMapper = userMapper;
        this.stateMachine = stateMachine;
    }

    @Override
    public void handle(Message message) {
        final var telegramUser = message.getFrom();
        final var userDto = userMapper.fromTgUserToUserDto(telegramUser);
        final var chatId = message.getChatId();

        final var registrationState = stateMachine.getUserState(userDto.getTelegramId());
        userDto.setState(registrationState);
        final var userMessage = stateMachine.handleState(userDto.getTelegramId(), userDto);

        messageSender.sendMessage(String.valueOf(chatId), userMessage);
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
