package kls.tgb.config;

import kls.tgb.telegram.commandhandlers.CommandHandler;
import kls.tgb.telegram.commandhandlers.CommandStartHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.util.Pair;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Configuration
public class BotCommandConfig {

    @Bean
    public List<BotCommand> botCommands(List<Pair<String, String>> pairs) {
        return pairs.stream().map(p -> new BotCommand(p.getFirst(), p.getSecond())).toList();
    }

    @Bean
    public Map<String, CommandHandler> commandHandlerMap(
            CommandStartHandlerImpl commandStartHandler) {
        final var pairs = List.of(Pair.of(commandStartHandler.getHandlerName(), commandStartHandler.getHandlerDescription()));
        botCommands(pairs);
        return new ConcurrentHashMap<>(Map.of(
                commandStartHandler.getHandlerName(), commandStartHandler
        ));
    }

}
