package kls.tgb.telegram;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@AllArgsConstructor
public class CommandStartHandlerImpl implements CommandHandler {

    private final MessageSender messageSender;

    @Override
    public void handle(Message message) {
        messageSender.sendMessage(String.valueOf(message.getChatId()), "приветик");
    }
}
