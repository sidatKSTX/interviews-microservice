package com.consultingfirm.interviews.repository;

import com.consultingfirm.interviews.model.InterviewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewsRepository extends JpaRepository<InterviewsEntity, Long> {
    // JpaRepository provides CRUD methods
}