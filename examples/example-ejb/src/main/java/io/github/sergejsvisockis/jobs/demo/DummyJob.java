package io.github.sergejsvisockis.jobs.demo;

import io.github.sergejsvisockis.jobs.AbstractJob;
import io.github.sergejsvisockis.jobs.JobExecutionException;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class DummyJob extends AbstractJob {

    private static final Logger LOG = LoggerFactory.getLogger(DummyJob.class);

    @EJB
    private JobConfigFactory factory;

    @PostConstruct
    public void postConstruct() {
        setJobRepository(factory.createRepository());
        setLockRepository(factory.createLockRepository());
    }

    @Schedule(second = "*/4", minute = "*", hour = "*")
    @Override
    public void run() {
        super.run();
    }

    @Override
    public void execute() throws JobExecutionException {
        LOG.info("Executing dummy job");
    }

    @Override
    public String getJobName() {
        return "DummyJob";
    }
}
