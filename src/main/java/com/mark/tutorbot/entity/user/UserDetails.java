package com.mark.tutorbot.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID id;

    @Column(name = "username")
    String username;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "registered_at")
    LocalDateTime registeredAt;

    @Column(name = "now_updating_timetable")
    String timetableId;

}
