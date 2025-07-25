package kls.tgb.telegram.userinputhandlers;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface TextMessageHandler {

    void handleTextMessage(Message messageText);

}
