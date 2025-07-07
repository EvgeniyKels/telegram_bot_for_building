package kls.tgb.config;

import kls.tgb.telegram.CommandHandler;
import kls.tgb.telegram.CommandHelpHandlerImpl;
import kls.tgb.telegram.CommandStartHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static kls.tgb.util.StringConstants.*;

@Configuration
public class BotCommandConfig {

    @Bean
    public List<BotCommand> botCommands() {
        return List.of(
                new BotCommand(SLASH.concat(START), "Регистрация/приветствие"),
                new BotCommand(SLASH.concat(HELP), "Справка по командам"));
    }

    @Bean
    public Map<String, CommandHandler> commandHandlerMap(
            CommandStartHandlerImpl commandStartHandler,
            CommandHelpHandlerImpl commandHelpHandler) {
        return new ConcurrentHashMap<>(Map.of(
                START, commandStartHandler,
                HELP, commandHelpHandler
        ));
    }

}
