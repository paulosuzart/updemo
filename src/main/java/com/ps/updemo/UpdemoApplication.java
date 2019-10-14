package com.ps.updemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class UpdemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(UpdemoApplication.class, args);
  }

}
