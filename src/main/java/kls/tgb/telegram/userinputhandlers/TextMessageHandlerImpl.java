package kls.tgb.telegram.userinputhandlers;

import kls.tgb.dto.sm.State;
import kls.tgb.mapper.UserMapper;
import kls.tgb.service.StateMachineImpl;
import kls.tgb.telegram.MessageSender;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@AllArgsConstructor
public class TextMessageHandlerImpl implements TextMessageHandler {

    private final StateMachineImpl stateMachine;
    private final UserMapper userMapper;
    private final MessageSender messageSender;

    @Override
    public void handleTextMessage(Message message) {
        final var telegramUser = message.getFrom();
        final var userDto = userMapper.fromTgUserToUserDto(telegramUser);
        final var chatId = message.getChatId();

        final var registrationState = stateMachine.getUserState(userDto.getTelegramId());
        userDto.setState(registrationState);
        if (State.WAITING_FOR_NAME.equals(registrationState)) {
            userDto.setSelfUserName(message.getText());
        } if (State.BLANK_PROJECT_CREATED.equals(registrationState)) {
            userDto.setNewProjectName(message.getText());
        }

        final var userMessage = stateMachine.handleState(userDto.getTelegramId(), userDto);

        messageSender.sendMessage(String.valueOf(chatId), userMessage);
    }

}
