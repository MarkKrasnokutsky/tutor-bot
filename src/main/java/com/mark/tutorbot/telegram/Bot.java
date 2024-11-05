package com.mark.tutorbot.telegram;

import com.mark.tutorbot.service.UpdateDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class Bot extends TelegramWebhookBot {

    private UpdateDispatcher dispatcher;
    private TelegramProps props;

    public Bot(TelegramProps props, UpdateDispatcher dispatcher) {
        super(props.getToken());
        this.props = props;
        this.dispatcher = dispatcher;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return dispatcher.distribute(update, this);
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
