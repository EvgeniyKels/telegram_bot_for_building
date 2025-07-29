package kls.tgb.telegram.userinputhandlers;

import kls.tgb.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@AllArgsConstructor
public class UserInputHandlerUtils {

    private final UserMapper userMapper;

    public PreHandleDataHolder prepareHandlerData(Message message) {
        final var telegramUser = message.getFrom();
        final var userDto = userMapper.fromTgUserToUserDto(telegramUser);
        final var chatId = message.getChatId();
        userDto.setChatId(chatId);
        return new PreHandleDataHolder(userDto, chatId);
    }

    public PreHandleDataHolder prepareHandlerData(CallbackQuery callbackQuery) {
        final var telegramUser = callbackQuery.getFrom();
        final var userDto = userMapper.fromTgUserToUserDto(telegramUser);
        final var chatId = callbackQuery.getMessage().getChatId();
        userDto.setChatId(chatId);
        return new PreHandleDataHolder(userDto, chatId);
    }
}
