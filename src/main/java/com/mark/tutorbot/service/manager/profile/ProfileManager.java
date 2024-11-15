package com.mark.tutorbot.service.manager.profile;

import com.mark.tutorbot.entity.user.Role;
import com.mark.tutorbot.entity.user.User;
import com.mark.tutorbot.entity.user.UserDetails;
import com.mark.tutorbot.repository.UserRepository;
import com.mark.tutorbot.service.factory.AnswerMethodFactory;
import com.mark.tutorbot.service.factory.KeyboardFactory;
import com.mark.tutorbot.service.manager.AbstractManager;
import com.mark.tutorbot.telegram.Bot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.mark.tutorbot.service.data.CallbackData.PROFILE_REFRESH_TOKEN;

@Component
@RequiredArgsConstructor
public class ProfileManager extends AbstractManager {

    private final UserRepository userRepository;
    private final AnswerMethodFactory answerMethodFactory;
    private final KeyboardFactory keyboardFactory;

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return showProfile(message);
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        switch (callbackQuery.getData()) {
            case PROFILE_REFRESH_TOKEN -> {
                return refreshToken(callbackQuery);
            }
        }
        return null;
    }

    private BotApiMethod<?> showProfile(Message message) {
        return answerMethodFactory.getSendMessage(
                message.getChatId(),
                getProfileText(message.getChatId()),
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Обновить токен"),
                        List.of(1),
                        List.of(PROFILE_REFRESH_TOKEN)
                ));
    }

    private BotApiMethod<?> showProfile(CallbackQuery callbackQuery) {
        return answerMethodFactory.getEditMessageText(
                callbackQuery,
                getProfileText(callbackQuery.getMessage().getChatId()),
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Обновить токен"),
                        List.of(1),
                        List.of(PROFILE_REFRESH_TOKEN)
                ));
    }

    private String getProfileText(Long chatId) {
        StringBuilder text = new StringBuilder("\uD83D\uDC64 Профиль\n");
        User user = userRepository.findById(chatId).orElseThrow();
        UserDetails details = user.getDetails();

        if (details.getUsername() != null) {
            text.append("* Имя пользователя - ").append(details.getUsername());
        }
        else {
            text.append("* Имя пользователя - ").append(details.getFirstName());
        }
        if (user.getRole().equals(Role.TEACHER)) {
            text.append("\n* Роль - Преподаватель");
        }
        if (user.getRole().equals(Role.STUDENT)) {
            text.append("\n* Роль - Ученик");
        }
        text.append("\n* Уникальный токен - ").append(user.getToken().toString());
        text.append("\n\n* \uFE0F - токен необходим для того, чтобы ученик или преподаватель могли установиться между собой связь");
        return text.toString();
    }

    private BotApiMethod<?> refreshToken(CallbackQuery callbackQuery) {
        User user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        user.refreshToken();
        userRepository.save(user);
        return showProfile(callbackQuery);
    }
}
