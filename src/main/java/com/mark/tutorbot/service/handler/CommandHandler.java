package com.mark.tutorbot.service.handler;

import com.mark.tutorbot.service.data.Command;
import com.mark.tutorbot.service.manager.feedback.FeedbackManager;
import com.mark.tutorbot.service.manager.help.HelpManager;
import com.mark.tutorbot.service.manager.progress_control.ProgressControlManager;
import com.mark.tutorbot.service.manager.start.StartManager;
import com.mark.tutorbot.service.manager.task.TaskManager;
import com.mark.tutorbot.service.manager.timetable.TimetableManager;
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

    @Autowired
    private final TimetableManager timetableManager;

    @Autowired
    private final TaskManager taskManager;

    @Autowired
    private final ProgressControlManager progressControlManager;

    public BotApiMethod<?> answer(Message message, Bot bot) {
        switch (message.getText()) {
            case Command.START -> {
                return start(message, bot);
            }
            case Command.FEEDBACK -> {
                return feedbackManager.answerCommand(message, bot);
            }
            case Command.HELP -> {
                return helpManager.answerCommand(message, bot);
            }
            case Command.TIMETABLE -> {
                return timetableManager.answerCommand(message, bot);
            }
            case Command.TASK -> {
                return taskManager.answerCommand(message, bot);
            }
            case Command.PROGRESS -> {
                return progressControlManager.answerCommand(message, bot);
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

    private BotApiMethod<?> start(Message message, Bot bot) {
        return startManager.answerCommand(message, bot);
    }


}
