package com.mark.tutorbot.service.handler;

import com.mark.tutorbot.entity.user.User;
import com.mark.tutorbot.repository.UserRepository;
import com.mark.tutorbot.service.manager.search.SearchManager;
import com.mark.tutorbot.telegram.Bot;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@RequiredArgsConstructor
public class MessageHandler {

    @Autowired
    private final SearchManager searchManager;

    @Autowired
    private final UserRepository userRepository;

    public BotApiMethod<?> answer(Message message, Bot bot) {
        User user = userRepository.findUserByChatId(message.getChatId());
        switch (user.getAction()) {
            case SENDING_TOKEN -> {
                return searchManager.answerMessage(message, bot);
            }
        }
        return null;
    }

}
