package kls.tgb.config;

import kls.tgb.telegram.MyTelegramBuildBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitializer {

    private final MyTelegramBuildBot bot;

    @Autowired
    public BotInitializer(MyTelegramBuildBot bot) {
        this.bot = bot;
    }

    @EventListener(classes = ContextRefreshedEvent.class)
    public void init() {
        try {
            final var telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
        } catch (Exception e) {
            //TODO добавить логирование
        }
    }

}
