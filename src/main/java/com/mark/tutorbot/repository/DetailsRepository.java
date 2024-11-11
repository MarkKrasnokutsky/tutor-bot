package com.mark.tutorbot.repository;

import com.mark.tutorbot.entity.user.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DetailsRepository extends JpaRepository<UserDetails, UUID> {



}
