package kls.tgb.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class CommandStartHandlerImpl implements CommandHandler {

    @Autowired
    private MyTelegramBuildBot telegramBot;

    @Override
    public void handle(Message message) {
        telegramBot.sendMessage(
                String.valueOf(message.getChatId()),
                "приветик"
        );
    }
}
