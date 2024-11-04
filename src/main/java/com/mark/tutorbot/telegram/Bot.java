package com.mark.tutorbot.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class Bot extends TelegramWebhookBot {

    private TelegramProps props;

    public Bot(TelegramProps props) {
        super(props.getToken());
        this.props = props;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

    @Override
    public String getBotPath() {
        return props.getPath();
    }

    @Override
    public String getBotUsername() {
        return props.getUsername();
    }
}
