package com.mark.tutorbot.entity.timetable;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "timetable")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeTable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "title")
    String title;

    @Column(name = "description")
    String description;

    @Enumerated(EnumType.STRING)
    WeekDay weekDay;

    @Column(name = "hour")
    Short hour;

    @Column(name = "minute")
    Short minute;


}
