package io.github.sergejsvisockis.jobs.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RetryJdbcBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(RetryJdbcBootstrap.class, args);
    }
}
