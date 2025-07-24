package io.github.sergejsvisockis.jobs.zookeeperlock;

import io.github.sergejsvisockis.jobs.lockrepository.LockRepository;
import io.github.sergejsvisockis.jobs.zookeeper.lockrepository.ZooKeeperLockRepository;
import io.github.sergejsvisockis.jobs.repository.JdbcJobRepository;
import io.github.sergejsvisockis.jobs.repository.JobRepository;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class Config {

    @Bean
    public JobRepository jobRepository(DataSource dataSource) {
        return new JdbcJobRepository(dataSource);
    }

    @Bean
    public ZooKeeper zooKeeper(@Value("${zookeeper.host}") String host,
                               @Value("${zookeeper.session-timeout}") Integer sessionTimeout) {
        try {
            return new ZooKeeper(host, sessionTimeout, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to Zookeeper", e);
        }
    }

    @Bean
    public LockRepository zooKeeperLockRepository(ZooKeeper zooKeeper) {
        return new ZooKeeperLockRepository(zooKeeper);
    }

}
