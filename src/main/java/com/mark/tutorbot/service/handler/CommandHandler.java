package com.mark.tutorbot.service.handler;

import com.mark.tutorbot.telegram.Bot;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class CommandHandler {

    public BotApiMethod<?> answer(Message message, Bot bot) {
        switch (message.getText()) {
            case "/start" -> {
                return start(message);
            }
        }
        return null;
    }

    private BotApiMethod<?> start(Message message) {
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text("""
                        🖖Приветствую в Tutor-Bot, инструменте для упрощения взаимодействия репетитора и ученика.
                        
                        Что бот умеет?
                        📌 Составлять расписание
                        📌 Прикреплять домашние задания
                        📌 Ввести контроль успеваемости
                        """)
                .build();
    }


}
