package kls.tgb.telegram;

import kls.tgb.exception.CommandHandlerException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

import static java.util.Objects.isNull;
import static kls.tgb.util.StringConstants.CANT_FIND_COMMAND_HANDLER;
import static kls.tgb.util.StringConstants.SLASH;

@Component
@AllArgsConstructor
public class BotDispatcher implements Dispatcher {

    private final Map<String, CommandHandler> commandHandlerMap;

    @Override
    public void dispatch(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        }
    }

    private void handleMessage(Message message) {
        final var text = message.getText();
        if (text.startsWith(SLASH)) {
            final var command = text.split(" ")[0].substring(1);
            final var commandHandler = commandHandlerMap.get(command);
            if (isNull(commandHandler)) {
                throw new CommandHandlerException(CANT_FIND_COMMAND_HANDLER, message.getChatId());
            }
            commandHandler.handle(message);
        }
    }

    private void handleCallback(CallbackQuery callbackQuery) {
        //TODO not implemented
    }

}
