package com.mark.tutorbot.service.manager;

import com.mark.tutorbot.service.factory.AnswerMethodFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class FeedbackManager {

    @Autowired
    private final AnswerMethodFactory answerMethodFactory;

    public BotApiMethod<?> answerCommand(Message message) {
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

    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery) {
        return answerMethodFactory.getEditMessageText(
                callbackQuery,
               """
                        📍 Ссылки для обратной связи
                        GitHub - https://github.com/markkrasnokutsky
                        Telegram - https://t.me/emberquency
                        """,
                null);
    }

}
