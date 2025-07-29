package kls.tgb.telegram.userinputhandlers;

import kls.tgb.dto.UserDto;
import kls.tgb.dto.sm.StartCommandState;
import kls.tgb.mapper.UserMapper;
import kls.tgb.service.StateMachine;
import kls.tgb.telegram.MessageSender;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@AllArgsConstructor
public class TextMessageHandlerImpl implements TextMessageHandler {

    private final StateMachine<UserDto> stateMachine;
    private final UserMapper userMapper;
    private final MessageSender messageSender;

    @Override
    public void handleTextMessage(Message message) {
        final var telegramUser = message.getFrom();
        final var userDto = userMapper.fromTgUserToUserDto(telegramUser);
        final var chatId = message.getChatId();
        userDto.setChatId(chatId);

        final var registrationState = stateMachine.getUserState(userDto.getTelegramId());
        userDto.setState(registrationState);
        if (StartCommandState.WAITING_FOR_NAME.equals(registrationState)) {
            userDto.setSelfUserName(message.getText());
        } if (StartCommandState.BLANK_PROJECT_CREATED.equals(registrationState)) {
            userDto.setNewProjectName(message.getText());
        }

        final var userMessage = stateMachine.handleStartCommandStates(userDto.getTelegramId(), userDto);

        messageSender.sendMessage(String.valueOf(chatId), userMessage);
    }

}
