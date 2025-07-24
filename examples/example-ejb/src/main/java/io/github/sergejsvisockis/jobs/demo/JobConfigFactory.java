package io.github.sergejsvisockis.jobs.demo;

import io.github.sergejsvisockis.jobs.lockrepository.JdbcLockRepository;
import io.github.sergejsvisockis.jobs.lockrepository.LockRepository;
import io.github.sergejsvisockis.jobs.repository.JdbcJobRepository;
import io.github.sergejsvisockis.jobs.repository.JobRepository;
import jakarta.annotation.Resource;
import jakarta.ejb.Singleton;

import javax.sql.DataSource;

@Singleton
public class JobConfigFactory {

    @Resource(lookup = "java:jboss/datasources/JobSampleDS")
    private DataSource dataSource;

    public JobRepository createRepository() {
        return new JdbcJobRepository(dataSource);
    }

    public LockRepository createLockRepository() {
        return new JdbcLockRepository(dataSource);
    }
}
