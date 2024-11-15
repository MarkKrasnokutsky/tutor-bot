package com.mark.tutorbot.service.manager.search;

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
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.mark.tutorbot.service.data.CallbackData.SEARCH_CANCEL;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchManager extends AbstractManager {

    private final AnswerMethodFactory answerMethodFactory;
    private final UserRepository userRepository;
    private final KeyboardFactory keyboardFactory;

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return askToken(message);
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        try {
            bot.execute(answerMethodFactory.getDeleteMessage(
                    message.getChatId(),
                    message.getMessageId() - 1
            ));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        User user = userRepository.findUserByChatId(message.getChatId());
        switch (user.getAction()) {
            case SENDING_TOKEN -> {
                return checkToken(message, user);
            }
        }
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        switch (callbackQuery.getData()) {
            case SEARCH_CANCEL -> {
                try {
                    return cancel(callbackQuery, bot);
                } catch (TelegramApiException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return null;
    }

    private BotApiMethod<?> checkToken(Message message, User user) {
        String token = message.getText();
        User userTwo = userRepository.findUserByToken(token);
        if (userTwo == null) {
            return answerMethodFactory.getSendMessage(
                    message.getChatId(),
                    """
                            По данному токену не найдено ни одного пользователя.
                            Повторите попытку
                            """,
                    keyboardFactory.getInlineKeyboardMarkup(
                            List.of("Отменить действие"),
                            List.of(1),
                            List.of(SEARCH_CANCEL)
                    )
            );
        }
        if (validation(user, userTwo)) {
            if (user.getRole() == Role.TEACHER) {
                user.addUser(userTwo);
            }
            else {
                userTwo.addUser(user);
            }
            user.setAction(Action.FREE);
            userRepository.save(user);
            userRepository.save(userTwo);
            return answerMethodFactory.getSendMessage(
                    message.getChatId(),
                    """
                            Связь успешно установлена
                            """,
                    null
            );
        }
        return answerMethodFactory.getSendMessage(
                message.getChatId(),
                """
                        Ошибка связи.
                        Вы не обладаете правами привязки к данному пользователю
                        """,
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Отменить действие"),
                        List.of(1),
                        List.of(SEARCH_CANCEL)
                )
        );
    }

    private boolean validation(User userOne, User userTwo) {
        return userOne.getRole() != userTwo.getRole();
    }

    private BotApiMethod<?> cancel(CallbackQuery callbackQuery, Bot bot) throws TelegramApiException {
        Long chatId = callbackQuery.getMessage().getChatId();
        User user = userRepository.findUserByChatId(chatId);
        user.setAction(Action.FREE);
        userRepository.save(user);
        bot.execute(answerMethodFactory.getAnswerCallbackQuery(
                callbackQuery.getId(),
                "Действие отменено"
        ));

        return answerMethodFactory.getDeleteMessage(chatId, callbackQuery.getMessage().getMessageId());
    }

    private BotApiMethod<?> askToken(Message message) {
        Long chatId = message.getChatId();
        User user = userRepository.findUserByChatId(chatId);
        user.setAction(Action.SENDING_TOKEN);
        userRepository.save(user);
        return answerMethodFactory.getSendMessage(
                chatId,
                """
                        Отправьте токен
                        """,
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Отменить действие"),
                        List.of(1),
                        List.of(SEARCH_CANCEL)
                )
        );
    }

}
