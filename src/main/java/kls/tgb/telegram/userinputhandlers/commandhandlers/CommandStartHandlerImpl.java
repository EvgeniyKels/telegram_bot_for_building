package kls.tgb.telegram.userinputhandlers.commandhandlers;

import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.StartCommandState;
import kls.tgb.mapper.UserMapper;
import kls.tgb.service.StateMachine;
import kls.tgb.telegram.MessageSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import static kls.tgb.util.StringConstants.*;

@Component
public class CommandStartHandlerImpl implements CommandHandler {

    private final MessageSender messageSender;
    private final StateMachine<UserDto, StartCommandState> stateMachine;
    private final UserMapper userMapper;

    @Value("${telegram.handler.description.start}")
    private String startHandlerDescription;

    public CommandStartHandlerImpl(MessageSender messageSender, StateMachine<UserDto, StartCommandState> stateMachine, UserMapper userMapper) {
        this.messageSender = messageSender;
        this.stateMachine = stateMachine;
        this.userMapper = userMapper;
    }

    @Override
    public void handle(Message message) {
        final var telegramUser = message.getFrom();
        final var userDto = userMapper.fromTgUserToUserDto(telegramUser);
        userDto.setChatId(message.getChatId());
        final var registrationState = stateMachine.getUserState(userDto.getTelegramId());
        userDto.setState(registrationState);
        final var messageButtonHolder = stateMachine.handleCommandStates(userDto.getTelegramId(), userDto);

        messageSender.sendMessage(String.valueOf(userDto.getChatId()), messageButtonHolder);
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
