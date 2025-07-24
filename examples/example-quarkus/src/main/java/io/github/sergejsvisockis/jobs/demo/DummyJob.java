package io.github.sergejsvisockis.jobs.demo;

import io.github.sergejsvisockis.jobs.AbstractJob;
import io.github.sergejsvisockis.jobs.JobExecutionException;
import io.quarkus.scheduler.Scheduled;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DummyJob extends AbstractJob {

    private static final Logger LOG = LoggerFactory.getLogger(DummyJob.class);

    public DummyJob(JobConfigFactory factory) {
        super(factory.createRepository(), factory.createLockRepository());
    }

    @Scheduled(every = "4s")
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
