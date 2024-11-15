package com.mark.tutorbot.repository;

import com.mark.tutorbot.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByChatId(Long chatId);

    User findUserByToken(String token);

}
