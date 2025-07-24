package io.github.sergejsvisockis.jobs.demo.mongo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MongoBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(MongoBootstrap.class, args);
    }

}
