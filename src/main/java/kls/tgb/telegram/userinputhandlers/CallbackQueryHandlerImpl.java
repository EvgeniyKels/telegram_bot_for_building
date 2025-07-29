package kls.tgb.telegram.userinputhandlers;

import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.Actions;
import kls.tgb.dto.sm.MessageButtonHolder;
import kls.tgb.mapper.UserMapper;
import kls.tgb.service.StateMachine;
import kls.tgb.telegram.MessageSender;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
@AllArgsConstructor
public class CallbackQueryHandlerImpl implements CallbackQueryHandler {

    private final StateMachine<UserDto> stateMachine;
    private final MessageSender messageSender;
    private final UserMapper userMapper;

    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery) {

        final var telegramUser = callbackQuery.getFrom();
        final var userDto = userMapper.fromTgUserToUserDto(telegramUser);
        final var chatId = callbackQuery.getMessage().getChatId();
        userDto.setChatId(chatId);

        final var registrationState = stateMachine.getUserState(userDto.getTelegramId());
        userDto.setState(registrationState);
        userDto.setUserAction(Actions.valueOf(callbackQuery.getData()));

        MessageButtonHolder messageButtonHolder = stateMachine.handleStartCommandStates(userDto.getTelegramId(), userDto);

        messageSender.sendMessage(String.valueOf(userDto.getChatId()), messageButtonHolder);

    }
}
