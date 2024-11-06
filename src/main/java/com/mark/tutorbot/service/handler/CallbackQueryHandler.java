package com.mark.tutorbot.service.handler;

import com.mark.tutorbot.service.manager.FeedbackManager;
import com.mark.tutorbot.service.manager.HelpManager;
import com.mark.tutorbot.telegram.Bot;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.mark.tutorbot.service.data.CallbackData.*;

@Service
@RequiredArgsConstructor
public class CallbackQueryHandler {

    @Autowired
    private final FeedbackManager feedbackManager;

    @Autowired
    private final HelpManager helpManager;


    public BotApiMethod<?> answer(CallbackQuery callbackQuery, Bot bot) {
        String callbackData = callbackQuery.getData();
        switch (callbackData) {
            case FEEDBACK -> {
                return feedbackManager.answerCallbackQuery(callbackQuery);
            }
            case HELP -> {
                return helpManager.answerCallbackQuery(callbackQuery);
            }
        }
        return null;
    }

}
