package io.github.sergejsvisockis.jobs.redislock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RedisLockBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(RedisLockBootstrap.class, args);
    }
}
