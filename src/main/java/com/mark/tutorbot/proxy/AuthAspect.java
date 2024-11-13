package com.mark.tutorbot.proxy;

import com.mark.tutorbot.entity.user.Action;
import com.mark.tutorbot.entity.user.Role;
import com.mark.tutorbot.entity.user.User;
import com.mark.tutorbot.repository.UserRepository;
import com.mark.tutorbot.service.manager.auth.AuthManager;
import com.mark.tutorbot.telegram.Bot;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Aspect
@Order(100)
@RequiredArgsConstructor
public class AuthAspect {

    private final UserRepository userRepository;
    private final AuthManager authManager;

    @Pointcut("execution(* com.mark.tutorbot.service.UpdateDispatcher.distribute(..))")
    public void distributeMethodPointcut() {}

    @Around("distributeMethodPointcut()")
    public Object authMethodAdvice(ProceedingJoinPoint pjp) throws Throwable {
        Update update = (Update) pjp.getArgs()[0];
        User user;

        if (update.hasMessage()) {
            user = userRepository.findById(update.getMessage().getChatId()).orElseThrow();
        }
        else if (update.hasCallbackQuery()) {
            user = userRepository.findById(update.getCallbackQuery().getMessage().getChatId()).orElseThrow();
        }
        else {
            return pjp.proceed();
        }

        if (user.getRole() != Role.GUEST) {
            return pjp.proceed();
        }

        if (user.getAction() == Action.AUTH) {
            return pjp.proceed();
        }
        return authManager.answerMessage(update.getMessage(), (Bot) pjp.getArgs()[1]);
    }

}
