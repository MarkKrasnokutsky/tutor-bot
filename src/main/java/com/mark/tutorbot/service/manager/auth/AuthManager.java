package com.mark.tutorbot.service.manager.auth;

import com.mark.tutorbot.entity.user.Action;
import com.mark.tutorbot.entity.user.Role;
import com.mark.tutorbot.entity.user.User;
import com.mark.tutorbot.repository.UserRepository;
import com.mark.tutorbot.service.factory.AnswerMethodFactory;
import com.mark.tutorbot.service.factory.KeyboardFactory;
import com.mark.tutorbot.service.manager.AbstractManager;
import com.mark.tutorbot.telegram.Bot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.mark.tutorbot.service.data.CallbackData.AUTH_STUDENT;
import static com.mark.tutorbot.service.data.CallbackData.AUTH_TEACHER;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthManager extends AbstractManager {

    private final AnswerMethodFactory answerMethodFactory;
    private final UserRepository userRepository;
    private final KeyboardFactory keyboardFactory;

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        User user = userRepository.findById(message.getChatId()).orElseThrow();
        user.setAction(Action.AUTH);
        userRepository.save(user);
        return answerMethodFactory.getSendMessage(message.getChatId(),
                """
                        Вы преподаватель или ученик ?
                        """,
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Ученик", "Преподаватель"),
                        List.of(2),
                        List.of(AUTH_STUDENT, AUTH_TEACHER))
                );
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        User user = userRepository.findById(chatId).orElseThrow();
        if (AUTH_TEACHER.equals(callbackQuery.getData())) {
            user.setRole(Role.TEACHER);
        }
        else {
            user.setRole(Role.STUDENT);
        }
        user.setAction(Action.FREE);
        userRepository.save(user);

        try {
            bot.execute(answerMethodFactory.getAnswerCallbackQuery(
                    callbackQuery.getId(),
                    """
                            Авторизация прошла успешно. Повторите предыдущее действие.
                            """
            ));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }

        return answerMethodFactory.getDeleteMessage(chatId, messageId);
    }
}
