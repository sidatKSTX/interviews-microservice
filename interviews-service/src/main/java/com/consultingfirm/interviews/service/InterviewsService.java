package com.consultingfirm.interviews.service;

import com.consultingfirm.interviews.dto.InterviewsInfo;
import com.consultingfirm.interviews.model.InterviewsEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface InterviewsService {
    void createInterviewDetails(MultipartFile file, int sheetIndex) throws Exception;

    InterviewsEntity createInterviews(InterviewsInfo interviewsInfo);

    void updateInterviewDetails(Long id, InterviewsEntity interviewsEntity);

    Optional<List<InterviewsEntity>> getInterviewDetails();

    Optional<InterviewsEntity> getInterviewDetailsByID(Long id);

    List<InterviewsEntity> getPaginatedInterviews(int page, int size, String sort);

    void deleteInterviewInfoById(long id);

    void deleteAllInterviewInfo();
}