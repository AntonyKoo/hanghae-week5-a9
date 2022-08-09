package com.example.intermediate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling  // 스케줄러 관련
@EnableJpaAuditing
@SpringBootApplication
public class IntermediateApplication {

  public static void main(String[] args) {
    SpringApplication.run(IntermediateApplication.class, args);
  }

}
