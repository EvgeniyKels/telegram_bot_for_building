package kls.tgb.telegram;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface CommandHandler {

    void handle(Message message);

}
