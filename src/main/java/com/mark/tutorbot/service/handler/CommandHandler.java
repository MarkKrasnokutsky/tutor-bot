package com.mark.tutorbot.service.handler;

import com.mark.tutorbot.service.data.Command;
import com.mark.tutorbot.service.manager.FeedbackManager;
import com.mark.tutorbot.service.manager.HelpManager;
import com.mark.tutorbot.service.manager.StartManager;
import com.mark.tutorbot.telegram.Bot;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;


@Service
@RequiredArgsConstructor
public class CommandHandler {

    @Autowired
    private final FeedbackManager feedbackManager;

    @Autowired
    private final HelpManager helpManager;

    @Autowired
    private final StartManager startManager;

    public BotApiMethod<?> answer(Message message, Bot bot) {
        switch (message.getText()) {
            case Command.START -> {
                return start(message);
            }
            case Command.FEEDBACK -> {
                return feedbackManager.answerCommand(message);
            }
            case Command.HELP -> {
                return helpManager.answerCommand(message);
            }
            default -> {
                return defaultAnswer(message);
            }
        }
    }

    private BotApiMethod<?> defaultAnswer(Message message) {
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text("""
                        Упс. Неизвестная команда :(
                        """)
                .build();
    }

    private BotApiMethod<?> start(Message message) {
        return startManager.answerCommand(message);
    }


}
