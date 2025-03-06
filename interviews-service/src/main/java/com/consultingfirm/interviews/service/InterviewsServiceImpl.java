package com.consultingfirm.interviews.service;

import com.consultingfirm.interviews.dto.InterviewsInfo;
import com.consultingfirm.interviews.exception.UserNotFoundException;
import com.consultingfirm.interviews.external.ExcelServiceProxy;
import com.consultingfirm.interviews.model.InterviewsEntity;
import com.consultingfirm.interviews.repository.InterviewsRepository;
import feign.FeignException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class InterviewsServiceImpl implements InterviewsService{

    private ExcelServiceProxy excelServiceProxy;

    private InterviewsRepository interviewsRepository;

    public InterviewsServiceImpl(ExcelServiceProxy excelServiceProxy, InterviewsRepository interviewsRepository) {
        this.excelServiceProxy = excelServiceProxy;
        this.interviewsRepository = interviewsRepository;
    }

    @Override
    public void createInterviewDetails(MultipartFile file, int sheetIndex) {
        try{
            List<InterviewsEntity> interviewsInfoList = excelServiceProxy.processExcel(file, sheetIndex);
            interviewsRepository.saveAll(interviewsInfoList);
        } catch (FeignException feignException){
            throw  new RuntimeException("Failed to process the uploaded excel", feignException);
        }
    }

    @Override
    public InterviewsEntity createInterviews(InterviewsInfo interviewsInfo) {
        InterviewsEntity interviewEntity = new InterviewsEntity();
        interviewEntity.setRecruiterName(interviewsInfo.recruiterName());
        interviewEntity.setRound(interviewsInfo.round());
        interviewEntity.setInterviewDate(interviewsInfo.interviewDate());
        interviewEntity.setInterviewTime(String.valueOf(interviewsInfo.interviewTime()));
        interviewEntity.setConsultantName(interviewsInfo.consultantName());
        interviewEntity.setOwnSupport(interviewsInfo.ownSupport());
        interviewEntity.setTechnology(interviewsInfo.technology());
        interviewEntity.setClientType(interviewsInfo.clientType());
        interviewEntity.setClientName(interviewsInfo.clientName());
        interviewEntity.setLocation(interviewsInfo.location());
        interviewEntity.setRate(String.valueOf(interviewsInfo.rate()));
        interviewEntity.setVendor(interviewsInfo.vendor());
        interviewEntity.setFeedback(interviewsInfo.feedback());
        interviewEntity.setComments(interviewsInfo.comments());
        return interviewsRepository.save(interviewEntity);
    }

    @Override
    public void updateInterviewDetails(Long id, InterviewsEntity interviewsEntity) {
        InterviewsEntity existingInterview = interviewsRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Interview not found for id: " + id));
        existingInterview.setRecruiterName(interviewsEntity.getRecruiterName());
        existingInterview.setRound(interviewsEntity.getRound());
        existingInterview.setInterviewDate(interviewsEntity.getInterviewDate());
        existingInterview.setInterviewTime(interviewsEntity.getInterviewTime());
        existingInterview.setConsultantName(interviewsEntity.getConsultantName());
        existingInterview.setOwnSupport(interviewsEntity.getOwnSupport());
        existingInterview.setTechnology(interviewsEntity.getTechnology());
        existingInterview.setClientType(interviewsEntity.getClientType());
        existingInterview.setClientName(interviewsEntity.getClientName());
        existingInterview.setLocation(interviewsEntity.getLocation());
        existingInterview.setRate(interviewsEntity.getRate());
        existingInterview.setVendor(interviewsEntity.getVendor());
        existingInterview.setFeedback(interviewsEntity.getFeedback());
        existingInterview.setComments(interviewsEntity.getComments());
        interviewsRepository.save(existingInterview);
    }

    @Override
    public Optional<List<InterviewsEntity>> getInterviewDetails() {
        return Optional.of(interviewsRepository.findAll());
    }

    @Override
    public Optional<InterviewsEntity> getInterviewDetailsByID(Long id) {
        return interviewsRepository.findById(id);
    }

    @Override
    public void deleteInterviewInfoById(long id) {
        interviewsRepository.deleteById(id);
    }

    @Override
    public void deleteAllInterviewInfo() {
        interviewsRepository.deleteAll();
    }

    @Override
    public List<InterviewsEntity> getPaginatedInterviews(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
        String sortBy = sortParams[0];
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<InterviewsEntity> resultPage = interviewsRepository.findAll(pageable);
        return resultPage.getContent();
    }
}