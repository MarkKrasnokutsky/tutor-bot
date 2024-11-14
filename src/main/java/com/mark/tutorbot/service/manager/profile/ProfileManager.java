package com.mark.tutorbot.service.manager.profile;

import com.mark.tutorbot.entity.user.Role;
import com.mark.tutorbot.entity.user.User;
import com.mark.tutorbot.entity.user.UserDetails;
import com.mark.tutorbot.repository.UserRepository;
import com.mark.tutorbot.service.factory.AnswerMethodFactory;
import com.mark.tutorbot.service.manager.AbstractManager;
import com.mark.tutorbot.telegram.Bot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class ProfileManager extends AbstractManager {

    private final UserRepository userRepository;
    private final AnswerMethodFactory answerMethodFactory;

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
        return null;
    }

    private BotApiMethod<?> showProfile(Message message) {
        StringBuilder text = new StringBuilder("\uD83D\uDC64 Профиль\n");
        User user = userRepository.findById(message.getChatId()).orElseThrow();
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
        else {
            text.append("\n* Роль - ").append(user.getRole().name());
        }
        text.append("\n* Уникальный токен - ").append(user.getToken().toString());
        text.append("\n\n* \uFE0F - токен необходим для того, чтобы ученик или преподаватель могли установиться между собой связь");
        return answerMethodFactory.getSendMessage(message.getChatId(), text.toString(), null);
    }
}
