package kls.tgb.telegram.userinputhandlers;

import kls.tgb.dto.TgUserChatDto;
import kls.tgb.dto.sm.UserAction;
import kls.tgb.mapper.UserMapper;
import kls.tgb.service.SMFactory;
import kls.tgb.service.StateMachine;
import kls.tgb.telegram.MessageSender;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
@AllArgsConstructor
public class CallbackQueryHandlerImpl implements CallbackQueryHandler {

    private final SMFactory smFactory;
    private final MessageSender messageSender;
    private final UserMapper userMapper;

    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        final var telegramUser = callbackQuery.getFrom();

        final var tgUserChatDto = new TgUserChatDto(
                telegramUser.getId(),
                callbackQuery.getMessage().getChatId(),
                telegramUser.getUserName(),
                UserAction.valueOf(callbackQuery.getData()),
                null
        );

        final var userMessage = smFactory.getStateMachineByState(tgUserChatDto.telegramID()).handleCommandStates(tgUserChatDto);

        messageSender.sendMessage(String.valueOf(tgUserChatDto.chatID()), userMessage);

    }
}
