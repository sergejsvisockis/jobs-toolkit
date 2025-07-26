package io.github.sergejsvisockis.jobs.demo;

import io.github.sergejsvisockis.jobs.lockrepository.JdbcLockRepository;
import io.github.sergejsvisockis.jobs.lockrepository.LockRepository;
import io.github.sergejsvisockis.jobs.repository.JdbcJobRepository;
import io.github.sergejsvisockis.jobs.repository.JobRepository;
import io.github.sergejsvisockis.jobs.retry.jdbc.repository.JdbcJobRetryHistoryRepository;
import io.github.sergejsvisockis.jobs.retry.repository.JobRetryHistoryRepository;
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
    public LockRepository lockRepository(DataSource dataSource) {
        return new JdbcLockRepository(dataSource);
    }

    @Bean
    public JobRetryHistoryRepository jobRetryHistoryRepository(DataSource dataSource) {
        return new JdbcJobRetryHistoryRepository(dataSource);
    }

}
