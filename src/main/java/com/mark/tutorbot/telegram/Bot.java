package com.mark.tutorbot.telegram;

import com.mark.tutorbot.service.UpdateDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class Bot extends TelegramWebhookBot {

    private final UpdateDispatcher dispatcher;

    private final TelegramProps telegramProps;

    public Bot(TelegramProps telegramProps, UpdateDispatcher dispatcher) {
        super(telegramProps.getToken());
        this.telegramProps = telegramProps;
        this.dispatcher = dispatcher;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return dispatcher.distribute(update, this);
    }

    @Override
    public String getBotPath() {
        return telegramProps.getPath();
    }

    @Override
    public String getBotUsername() {
        return telegramProps.getUsername();
    }
}
