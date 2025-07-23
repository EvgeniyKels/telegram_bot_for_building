package kls.tgb.telegram.commandhandlers;

import kls.tgb.service.DbService;
import kls.tgb.mapper.UserMapper;
import kls.tgb.telegram.MessageSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import static kls.tgb.util.StringConstants.*;

@Component
public class CommandStartHandlerImpl implements CommandHandler {

    private final MessageSender messageSender;
    private final DbService dbService;
    private final UserMapper userMapper;

    @Value("${telegram.handler.description.start}")
    private String startHandlerDescription;

    public CommandStartHandlerImpl(MessageSender messageSender, DbService dbService, UserMapper userMapper) {
        this.messageSender = messageSender;
        this.dbService = dbService;
        this.userMapper = userMapper;
    }

    @Override
    public void handle(Message message) {
        final var telegramUser = message.getFrom();
        final var userDto = dbService.registerOrUpdateUser(userMapper.fromTgUserToUserDto(telegramUser));
        messageSender.sendMessage(String.valueOf(message.getChatId()), "приветик, ".concat(userDto.getUsername())); //TODO приветственное сообщение получать из БД
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
