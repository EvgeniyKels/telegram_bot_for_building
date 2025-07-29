package kls.tgb.telegram.userinputhandlers.commandhandlers;

import kls.tgb.dto.UserDto;
import kls.tgb.mapper.UserMapper;
import kls.tgb.service.StateMachine;
import kls.tgb.telegram.MessageSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import static kls.tgb.util.StringConstants.*;

@Component
public class CommandReloadHandlerImpl implements CommandHandler{

    @Value("${telegram.handler.description.remove}")
    private String startHandlerDescription;

    private final StateMachine<UserDto> stateMachine;
    private final MessageSender messageSender;
    private final UserMapper userMapper;

    public CommandReloadHandlerImpl(StateMachine<UserDto> stateMachine, MessageSender messageSender, UserMapper userMapper) {
        this.stateMachine = stateMachine;
        this.messageSender = messageSender;
        this.userMapper = userMapper;
    }

    @Override
    public void handle(Message message) {
        final var telegramUser = message.getFrom();
        final var userDto = userMapper.fromTgUserToUserDto(telegramUser);
        final var chatId = message.getChatId();
        userDto.setChatId(chatId);
        stateMachine.removeUserState(userDto.getTelegramId());
        messageSender.sendMessage(String.valueOf(userDto.getChatId()), "удалено");
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
