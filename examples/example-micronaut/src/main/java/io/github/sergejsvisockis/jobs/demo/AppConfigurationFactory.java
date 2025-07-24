package io.github.sergejsvisockis.jobs.demo;

import io.github.sergejsvisockis.jobs.lockrepository.JdbcLockRepository;
import io.github.sergejsvisockis.jobs.lockrepository.LockRepository;
import io.github.sergejsvisockis.jobs.repository.JdbcJobRepository;
import io.github.sergejsvisockis.jobs.repository.JobRepository;
import jakarta.inject.Singleton;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

@Singleton
public class AppConfigurationFactory {

    private final DataSourceConfigProperties dbConfigProps;

    public AppConfigurationFactory(DataSourceConfigProperties dbConfigProps) {
        this.dbConfigProps = dbConfigProps;
    }

    public JobRepository jobRepository() {
        return new JdbcJobRepository(getDataSource());
    }

    public LockRepository lockRepository() {
        return new JdbcLockRepository(getDataSource());
    }

    public DataSource getDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerNames(new String[]{dbConfigProps.getServerName()});
        dataSource.setPortNumbers(new int[]{dbConfigProps.getPort()});
        dataSource.setDatabaseName(dbConfigProps.getDatabaseName());
        dataSource.setUser(dbConfigProps.getUser());
        dataSource.setPassword(dbConfigProps.getPassword());
        return dataSource;
    }

}
