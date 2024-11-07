package com.mark.tutorbot.service.handler;

import com.mark.tutorbot.service.manager.feedback.FeedbackManager;
import com.mark.tutorbot.service.manager.help.HelpManager;
import com.mark.tutorbot.service.manager.progress_control.ProgressControlManager;
import com.mark.tutorbot.service.manager.task.TaskManager;
import com.mark.tutorbot.service.manager.timetable.TimetableManager;
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

    @Autowired
    private final TimetableManager timetableManager;

    @Autowired
    private final TaskManager taskManager;

    @Autowired
    private final ProgressControlManager progressControlManager;


    public BotApiMethod<?> answer(CallbackQuery callbackQuery, Bot bot) {
        String callbackData = callbackQuery.getData();
        if (callbackData.split("_")[0].equals(TIMETABLE)) {
            return timetableManager.answerCallbackQuery(callbackQuery, bot);
        }
        if (callbackData.split("_")[0].equals(TASK)) {
            return taskManager.answerCallbackQuery(callbackQuery, bot);
        }
        if (callbackData.split("_")[0].equals(PROGRESS)) {
            return progressControlManager.answerCallbackQuery(callbackQuery, bot);
        }
        switch (callbackData) {
            case FEEDBACK -> {
                return feedbackManager.answerCallbackQuery(callbackQuery, bot);
            }
            case HELP -> {
                return helpManager.answerCallbackQuery(callbackQuery, bot);
            }
        }
        return null;
    }

}
