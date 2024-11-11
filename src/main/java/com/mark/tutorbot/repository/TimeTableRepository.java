package com.mark.tutorbot.repository;

import com.mark.tutorbot.entity.timetable.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, UUID> {
}
