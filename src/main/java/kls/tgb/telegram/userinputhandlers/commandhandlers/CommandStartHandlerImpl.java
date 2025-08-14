package kls.tgb.telegram.userinputhandlers.commandhandlers;

import kls.tgb.dto.TgUserChatDto;
import kls.tgb.service.StateMachine;
import kls.tgb.telegram.MessageSender;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import static kls.tgb.util.StringConstants.*;

@Component
public class CommandStartHandlerImpl implements CommandHandler {

    private final MessageSender messageSender;
    private final StateMachine stateMachine;

    @Value("${telegram.handler.description.start}")
    private String startHandlerDescription;

    public CommandStartHandlerImpl(MessageSender messageSender, @Qualifier(START_COMMAND_SM) StateMachine stateMachine) {
        this.messageSender = messageSender;
        this.stateMachine = stateMachine;
    }

    @Override
    public void handle(Message message) {
        final var telegramUser = message.getFrom();
        final var tgUserChatDto = new TgUserChatDto(
                telegramUser.getId(),
                message.getChatId(),
                telegramUser.getUserName(),
                null,
                null
        );
        final var messageButtonHolder = stateMachine.handleCommandStates(tgUserChatDto);

        messageSender.sendMessage(String.valueOf(tgUserChatDto.chatID()), messageButtonHolder);
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
