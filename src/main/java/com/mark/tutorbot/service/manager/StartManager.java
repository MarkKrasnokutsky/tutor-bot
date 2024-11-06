package com.mark.tutorbot.service.manager;

import com.mark.tutorbot.service.data.CallbackData;
import com.mark.tutorbot.service.factory.AnswerMethodFactory;
import com.mark.tutorbot.service.factory.KeyboardFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StartManager {

    @Autowired
    private final AnswerMethodFactory answerMethodFactory;

    @Autowired
    private final KeyboardFactory keyboardFactory;

    public SendMessage answerCommand(Message message) {
        return answerMethodFactory.getSendMessage(
                message.getChatId(),
                """
                        🖖Приветствую в Tutor-Bot, инструменте для упрощения взаимодействия репетитора и ученика.
                        
                        Что бот умеет?
                        📌 Составлять расписание
                        📌 Прикреплять домашние задания
                        📌 Ввести контроль успеваемости
                        """,
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Помощь", "Обратная связь"),
                        List.of(1,1),
                        List.of(CallbackData.HELP, CallbackData.FEEDBACK)));
    }

}
