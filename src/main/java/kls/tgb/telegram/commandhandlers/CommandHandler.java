package kls.tgb.telegram.commandhandlers;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface CommandHandler {

    void handle(Message message);

    String getHandlerName();

    String getHandlerDescription();

}
