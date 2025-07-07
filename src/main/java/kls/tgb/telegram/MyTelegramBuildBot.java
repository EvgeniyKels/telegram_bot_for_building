package kls.tgb.telegram;

import kls.tgb.config.BotProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MyTelegramBuildBot extends TelegramLongPollingBot {

    @Autowired
    private BotProperties botConfig;
    @Autowired
    private BotDispatcher dispatcher;

    @Override
    public void onUpdateReceived(Update update) {
        dispatcher.dispatch(update);
    }

    public void sendMessage(String chatId, String text) {
        SendMessage sendMessage = new SendMessage(chatId, text);
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
}
