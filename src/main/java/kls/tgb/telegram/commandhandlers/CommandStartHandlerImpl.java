package kls.tgb.telegram.commandhandlers;

import kls.tgb.dto.sm.RegistrationState;
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
    //TODO приветственное сообщение получать из БД
    public void handle(Message message) {
        final var telegramUser = message.getFrom();
        RegistrationState registrationState = dbService.getStateByTgID(telegramUser.getId());
        System.out.println(registrationState);
        switch (registrationState) {
            case STATE_NOT_EXISTS -> {
                dbService.setState(telegramUser.getId(), RegistrationState.WAITING_FOR_NAME); // предложить ввести свое имя, написать что то про бот
                messageSender.sendMessage(String.valueOf(message.getChatId()), "приветик, как тебя звать ?");
            }
            case WAITING_FOR_NAME -> {
                final var userDto = dbService.registerOrUpdateUser(userMapper.fromTgUserToUserDto(telegramUser));
                messageSender.sendMessage(String.valueOf(message.getChatId()), "приветик, ".concat(userDto.getUsername()));
            }
            case NEW_USER_REGISTERED -> {
                messageSender.sendMessage(String.valueOf(message.getChatId()), "поздравляю с регистрацией");
            }
            case USER_EXISTS -> {
                messageSender.sendMessage(String.valueOf(message.getChatId()), "юзер существует");
            }
//            case PROJECT_EXISTS -> null;
//            case PROJECT_NOT_EXISTS -> null;
//            case CHOOSE_PROJECT -> null;
//            case CREATING_PROJECT -> null;
//            case PROJECT_CREATED -> null;
        }

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
