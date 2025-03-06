package com.consultingfirm.interviews.controller;

import com.consultingfirm.interviews.dto.InterviewsInfo;
import com.consultingfirm.interviews.exception.UserNotFoundException;
import com.consultingfirm.interviews.model.InterviewsEntity;
import com.consultingfirm.interviews.service.InterviewsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/interviews")
public class InterviewsController {

    @Value("${interviews-excel-sheet-index}")
    private int interviewsExcelSheetIndex;
    private final InterviewsService interviewsService;

    public InterviewsController(InterviewsService interviewsService) {
        this.interviewsService = interviewsService;
    }

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    @Operation(summary = "Upload Archive Interview Details")
    public ResponseEntity<?> uploadExcel(@RequestParam("file") MultipartFile file){
        try {
            if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid file. Please upload a non-empty Excel file."));
            }
            interviewsService.createInterviewDetails(file, interviewsExcelSheetIndex);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Interviews Excel uploaded into Database Successfully"));
        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("Error","Interviews Excel upload Error: " + exception));
        }
    }

    @PostMapping()
    @Operation(summary = "Create Interviews")
    public ResponseEntity<InterviewsEntity> createInterviewInfo(@Valid @RequestBody InterviewsInfo interviewsInfo) {
        var newInterviewInfo = interviewsService.createInterviews(interviewsInfo);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(newInterviewInfo.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Interview Details")
    public ResponseEntity<String> updateInterviewInfo(@PathVariable Long id, @RequestBody InterviewsEntity interviewsEntity) {
        interviewsService.updateInterviewDetails(id, interviewsEntity);
        return new ResponseEntity<>("Interview details updated successfully.", HttpStatus.OK);
    }

    @GetMapping()
    @Operation(summary = "Fetch Interview Details")
    public ResponseEntity<List<InterviewsEntity>> fetchInterviewDetails() {
        Optional<List<InterviewsEntity>> interviews = interviewsService.getInterviewDetails();
        return interviews.map(interviewDetails -> new ResponseEntity<>(interviewDetails, HttpStatus.OK))
                .orElseThrow(() -> new UserNotFoundException("No interviews found.."));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Fetch Interview Details By ID")
    public ResponseEntity<Optional<InterviewsEntity>> fetchInterviewDetailsByID(@PathVariable Long id) {
        Optional<Optional<InterviewsEntity>> interview = Optional.ofNullable(interviewsService.getInterviewDetailsByID(id));
        return interview.map(interviewDetails -> new ResponseEntity<>(interviewDetails, HttpStatus.OK))
                .orElseThrow(() -> new UserNotFoundException("Interview not found for id: " + id));
    }

    @DeleteMapping()
    @Operation(summary = "Delete All Interviews")
    public ResponseEntity<HttpStatus> deleteAllInterviewInfo() {
        interviewsService.deleteAllInterviewInfo();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Interview By Id")
    public ResponseEntity<HttpStatus> deleteInterviewInfoById(@PathVariable("id") long id) {
        interviewsService.deleteInterviewInfoById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/paginated")
    @Operation(summary = "Fetch Paginated Interviews with Sorting")
    public ResponseEntity<List<InterviewsEntity>> fetchPaginatedInterviews(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sort) {

        List<InterviewsEntity> interviews = interviewsService.getPaginatedInterviews(page, size, sort);
        return ResponseEntity.ok(interviews);
    }
}