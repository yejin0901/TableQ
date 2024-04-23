package com.project.team11_tabling;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableBatchProcessing
public class Team11TablingApplication {

  public static void main(String[] args) {
    SpringApplication.run(Team11TablingApplication.class, args);
  }

}
