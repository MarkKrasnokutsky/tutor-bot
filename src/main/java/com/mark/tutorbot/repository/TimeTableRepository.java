package com.mark.tutorbot.repository;

import com.mark.tutorbot.entity.timetable.TimeTable;
import com.mark.tutorbot.entity.timetable.WeekDay;
import com.mark.tutorbot.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, UUID> {

    List<TimeTable> findAllByUsersContainingAndWeekDay(User user, WeekDay weekDay);
    TimeTable findTimeTableById(UUID id);

}
