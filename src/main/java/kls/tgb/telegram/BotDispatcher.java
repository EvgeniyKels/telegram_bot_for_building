package kls.tgb.telegram;

import kls.tgb.exception.CommandHandlerException;
import kls.tgb.telegram.commandhandlers.CommandHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

import static java.util.Objects.isNull;
import static kls.tgb.util.StringConstants.CANT_FIND_COMMAND_HANDLER;
import static kls.tgb.util.StringConstants.SLASH;

@Slf4j
@Component
@AllArgsConstructor
public class BotDispatcher implements Dispatcher {

    private final Map<String, CommandHandler> commandHandlerMap;

    @Override
    public void dispatch(final Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        }
    }

    private void handleMessage(final Message message) {
        log.info(message.getFrom().getId().toString());
        final var command = message.getText();
        if (command.startsWith(SLASH)) {
            final var commandHandler = commandHandlerMap.get(command);
            if (isNull(commandHandler)) {
                throw new CommandHandlerException(CANT_FIND_COMMAND_HANDLER, message.getChatId());
            }
            commandHandler.handle(message);
        } else {
            handleTextMessage();
        }
    }

    private void handleCallback(CallbackQuery callbackQuery) {
        //TODO not implemented
    }

}
