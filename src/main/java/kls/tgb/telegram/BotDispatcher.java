package kls.tgb.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;
import static kls.tgb.util.StringConstants.SLASH;

@Component
public class BotDispatcher implements Dispatcher {

    @Autowired
    private final Map<String, CommandHandler> commandHandlerMap = new ConcurrentHashMap<>();

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
                throw new RuntimeException("не найден обработчик команды");
            }
        }
    }

    private void handleCallback(CallbackQuery callbackQuery) {

    }

}
