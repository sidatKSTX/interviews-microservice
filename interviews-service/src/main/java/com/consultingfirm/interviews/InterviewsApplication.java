package com.consultingfirm.interviews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@RefreshScope
public class InterviewsApplication {
	public static void main(String[] args) {
		SpringApplication.run(InterviewsApplication.class, args);
	}
}