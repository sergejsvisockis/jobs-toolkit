package io.github.sergejsvisockis.jobs.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DynamoDBBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(DynamoDBBootstrap.class, args);
    }
}
