package kls.tgb.telegram;

import kls.tgb.dto.sm.MessageButtonHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class MessageSenderImpl implements MessageSender {

    // TODO нарушение d inversion
    private final MyTelegramBuildBot bot;

    @Autowired
    public MessageSenderImpl(@Lazy MyTelegramBuildBot bot) {
        this.bot = bot;
    }

    @Override
    public void sendMessage(String chatId, MessageButtonHolder messageButtonHolder) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(messageButtonHolder.message());
        sendMessage.setReplyMarkup(createKeyboard(messageButtonHolder.buttons()));
        bot.sendMessage(sendMessage);
    }

    @Override
    public void sendMessage(String chatId, String message) {
        bot.sendMessage(new SendMessage(chatId, message));
    }

    private ReplyKeyboard createKeyboard(Map<String, String> buttonMap) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        buttonMap.forEach((key, value) -> {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(key);
            button.setCallbackData(value);
            row.add(button);
            keyboard.add(row);
        });

        markup.setKeyboard(keyboard);

        return markup;
    }
}
