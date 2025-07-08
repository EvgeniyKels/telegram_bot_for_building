package kls.tgb.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class MessageSenderImpl implements MessageSender {

    private final MyTelegramBuildBot bot;

    @Autowired
    public MessageSenderImpl(@Lazy MyTelegramBuildBot bot) {
        this.bot = bot;
    }

    @Override
    public void sendMessage(String chatId, String textForAnswerMessage) {
        bot.sendMessage(new SendMessage(chatId, textForAnswerMessage));
    }
}
