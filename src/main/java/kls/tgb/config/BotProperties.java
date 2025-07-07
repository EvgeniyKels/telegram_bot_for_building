package kls.tgb.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class BotProperties {

    @Value("${telegram.bot.token}")
    private String token;

    @Value("${telegram.bot.username}")
    private String name;

}
