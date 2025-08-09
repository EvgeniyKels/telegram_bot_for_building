package kls.tgb.telegram.userinputhandlers;

import kls.tgb.dto.TgUserChatDto;
import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.StartCommandState;
import kls.tgb.mapper.UserMapper;
import kls.tgb.service.SMFactory;
import kls.tgb.service.StateMachine;
import kls.tgb.telegram.MessageSender;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@AllArgsConstructor
public class TextMessageHandlerImpl implements TextMessageHandler {

    private final SMFactory smFactory;
    private final MessageSender messageSender;

    @Override
    public void handleTextMessage(Message message) {
        final var telegramUser = message.getFrom();

        final var tgUserChatDto = new TgUserChatDto(
                telegramUser.getId(),
                message.getChatId(),
                telegramUser.getUserName(),
                null,
                message.getText()
        );

        final var userMessage = smFactory.
                getStateMachineByState(tgUserChatDto.chatID()).handleCommandStates(tgUserChatDto);

        messageSender.sendMessage(String.valueOf(tgUserChatDto.chatID()), userMessage);
    }

}
