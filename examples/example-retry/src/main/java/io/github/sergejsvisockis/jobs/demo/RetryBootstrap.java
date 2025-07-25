package io.github.sergejsvisockis.jobs.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RetryBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(RetryBootstrap.class, args);
    }
}
