package kls.tgb.telegram;

import kls.tgb.dto.sm.MessageButtonHolder;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface MessageSender {

    void sendMessage(String chatId, MessageButtonHolder messageButtonHolder);

    void sendMessage(String string, String userMessage);
}
