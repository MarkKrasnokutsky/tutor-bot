package com.mark.tutorbot.proxy;

import com.mark.tutorbot.entity.user.Action;
import com.mark.tutorbot.entity.user.Role;
import com.mark.tutorbot.entity.user.UserDetails;
import com.mark.tutorbot.repository.DetailsRepository;
import com.mark.tutorbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;

@Component
@Aspect
@Order(10)
@RequiredArgsConstructor
public class UserCreationAspect {

    private final UserRepository userRepository;
    private final DetailsRepository detailsRepository;

    @Pointcut("execution(* com.mark.tutorbot.service.UpdateDispatcher.distribute(..))")
    public void distributeMethodPointcut() {}

    @Around("distributeMethodPointcut()")
    public Object distributeMethodAdvice(ProceedingJoinPoint pjp) throws Throwable {
        Update update = (Update) pjp.getArgs()[0];
        User tgUser;

        if (update.hasMessage()) {
            tgUser = update.getMessage().getFrom();
        }
        else if (update.hasCallbackQuery()) {
            tgUser = update.getCallbackQuery().getFrom();
        }
        else {
            return pjp.proceed();
        }

        if (userRepository.existsById(tgUser.getId())) {
            return pjp.proceed();
        }

        UserDetails details = UserDetails
                .builder()
                .firstName(tgUser.getFirstName())
                .username(tgUser.getUserName())
                .lastName(tgUser.getLastName())
                .registeredAt(LocalDateTime.now())
                .build();

        detailsRepository.save(details);

        com.mark.tutorbot.entity.user.User newUser = com.mark.tutorbot.entity.user.User
                .builder()
                .chatId(tgUser.getId())
                .role(Role.GUEST)
                .action(Action.FREE)
                .details(details)
                .build();

        userRepository.save(newUser);

        return pjp.proceed();
    }

}
