package com.mark.tutorbot.service;

import com.mark.tutorbot.entity.User;
import com.mark.tutorbot.repository.UserRepository;
import com.mark.tutorbot.service.handler.CallbackQueryHandler;
import com.mark.tutorbot.service.handler.CommandHandler;
import com.mark.tutorbot.service.handler.MessageHandler;
import com.mark.tutorbot.telegram.Bot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateDispatcher {

    private final MessageHandler messageHandler;
    private final CommandHandler commandHandler;
    private final CallbackQueryHandler callbackQueryHandler;
    private final UserRepository userRepository;

    public BotApiMethod<?> distribute(Update update, Bot bot) {
        if (update.hasCallbackQuery()) {
            return callbackQueryHandler.answer(update.getCallbackQuery(), bot);
        }
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText() && message.getText().startsWith("/")) {
                userRepository.save(User.builder()
                                .chatId(message.getChatId())
                        .build());
                return commandHandler.answer(message, bot);
            }
            return messageHandler.answer(message, bot);
        }
        log.info("Unsupported update: " + update);
        return null;
    }
}
