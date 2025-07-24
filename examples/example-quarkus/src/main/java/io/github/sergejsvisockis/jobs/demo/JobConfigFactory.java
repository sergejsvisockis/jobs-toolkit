package io.github.sergejsvisockis.jobs.demo;

import io.github.sergejsvisockis.jobs.lockrepository.JdbcLockRepository;
import io.github.sergejsvisockis.jobs.lockrepository.LockRepository;
import io.github.sergejsvisockis.jobs.repository.JdbcJobRepository;
import io.github.sergejsvisockis.jobs.repository.JobRepository;
import jakarta.inject.Singleton;

import javax.sql.DataSource;

@Singleton
public class JobConfigFactory {

    private final DataSource dataSource;

    public JobConfigFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public JobRepository createRepository() {
        return new JdbcJobRepository(dataSource);
    }

    public LockRepository createLockRepository() {
        return new JdbcLockRepository(dataSource);
    }
}
