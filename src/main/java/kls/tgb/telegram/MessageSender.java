package kls.tgb.telegram;

import kls.tgb.dto.sm.MessageButtonHolder;

public interface MessageSender {

    void sendMessage(String chatId, MessageButtonHolder messageButtonHolder);

    void sendMessage(String string, String userMessage);
}
