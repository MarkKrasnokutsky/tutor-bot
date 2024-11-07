package com.mark.tutorbot.service.manager.feedback;

import com.mark.tutorbot.service.factory.AnswerMethodFactory;
import com.mark.tutorbot.service.manager.AbstractManager;
import com.mark.tutorbot.telegram.Bot;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class FeedbackManager extends AbstractManager {

    @Autowired
    private final AnswerMethodFactory answerMethodFactory;

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return answerMethodFactory.getSendMessage(
                message.getChatId(),
                """
                        📍 Ссылки для обратной связи
                        GitHub - https://github.com/markkrasnokutsky
                        Telegram - https://t.me/emberquency
                        """,
                null
                );
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        return answerMethodFactory.getEditMessageText(
                callbackQuery,
               """
                        📍 Ссылки для обратной связи
                        GitHub - https://github.com/markkrasnokutsky
                        Telegram - https://t.me/emberquency
                        """,
                null);
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

}
