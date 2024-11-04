package com.mark.tutorbot.telegram;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "telegram-bot")
@Data
public class TelegramProps {
    private String token;
    private String username;
    private String path;

}
