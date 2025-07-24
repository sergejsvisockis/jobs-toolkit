package io.github.sergejsvisockis.jobs.zookeeperlock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ZookeeperLockBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(ZookeeperLockBootstrap.class, args);
    }
}
