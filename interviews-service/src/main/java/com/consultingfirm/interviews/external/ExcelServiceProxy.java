package com.consultingfirm.interviews.external;

import com.consultingfirm.interviews.model.InterviewsEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//@FeignClient(name = "common-excel-service", url = "${excel.processor.url}")
@FeignClient(name = "common-excel-service")
public interface ExcelServiceProxy {
    @PostMapping(value = "/api/process/interviews", consumes = {"multipart/form-data"})
    List<InterviewsEntity> processExcel(@RequestPart("file") MultipartFile file,
                                        @RequestParam("sheetIndex") int sheetIndex);
}