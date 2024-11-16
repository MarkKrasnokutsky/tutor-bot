package com.mark.tutorbot.service.manager.task;

import com.mark.tutorbot.service.factory.AnswerMethodFactory;
import com.mark.tutorbot.service.factory.KeyboardFactory;
import com.mark.tutorbot.service.manager.AbstractManager;
import com.mark.tutorbot.telegram.Bot;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.mark.tutorbot.service.data.CallbackData.*;
import static com.mark.tutorbot.service.data.CallbackData.TIMETABLE_REMOVE;

@Component
@RequiredArgsConstructor
public class TaskManager extends AbstractManager {

    @Autowired
    private final AnswerMethodFactory answerMethodFactory;

    @Autowired
    private final KeyboardFactory keyboardFactory;

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return mainMenu(message);
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        switch (callbackQuery.getData()) {
            case TASK -> {
                return mainMenu(callbackQuery);
            }
            case TASK_CREATE -> {
                return add(callbackQuery);
            }
        }
        return null;
    }

    private BotApiMethod<?> mainMenu(Message message) {
        return answerMethodFactory.getSendMessage(
                message.getChatId(),
                """
                        🗂 Вы можете добавить домашнее задание вашему ученику
                        """,
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Прикрепить домашнее задание"),
                        List.of(1),
                        List.of(TASK_CREATE)
                )
        );
    }

    private BotApiMethod<?> mainMenu(CallbackQuery callbackQuery) {
        return answerMethodFactory.getEditMessageText(
                callbackQuery,
                """
                        🗂 Вы можете добавить домашнее задание вашему ученику
                        """,
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Прикрепить домашнее задание"),
                        List.of(1),
                        List.of(TASK_CREATE)
                )
        );
    }

    private BotApiMethod<?> add(CallbackQuery callbackQuery) {
        return answerMethodFactory.getEditMessageText(
                callbackQuery,
                """
                        👤 Выберете ученика, которому хотите дать домашнее задание
                        """,
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Назад"),
                        List.of(1),
                        List.of(TASK)
                )
        );
    }
}
