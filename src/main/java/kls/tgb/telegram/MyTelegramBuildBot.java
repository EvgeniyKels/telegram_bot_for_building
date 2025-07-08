package kls.tgb.telegram;

import kls.tgb.config.BotProperties;
import kls.tgb.exception.MessageSenderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class MyTelegramBuildBot extends TelegramLongPollingBot {

    private final BotProperties botConfig;
    private final BotDispatcher dispatcher;

    @Autowired
    public MyTelegramBuildBot(BotProperties botConfig, BotDispatcher dispatcher) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.dispatcher = dispatcher;
    }

    @Override
    public void onUpdateReceived(Update update) {
        dispatcher.dispatch(update);
    }

    public void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            throw new MessageSenderException(e.getMessage(), Long.parseLong(sendMessage.getChatId()));
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

}
