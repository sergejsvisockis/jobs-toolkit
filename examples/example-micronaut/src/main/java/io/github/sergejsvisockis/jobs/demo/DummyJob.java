package io.github.sergejsvisockis.jobs.demo;

import io.github.sergejsvisockis.jobs.AbstractJob;
import io.github.sergejsvisockis.jobs.JobExecutionException;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DummyJob extends AbstractJob {

    private static final Logger LOG = LoggerFactory.getLogger(DummyJob.class);

    public DummyJob(AppConfigurationFactory configFactory) {
        super(configFactory.jobRepository(), configFactory.lockRepository());
    }

    @Scheduled(fixedRate = "1s")
    @Override
    public void run() {
        super.run();
    }

    @Override
    public void execute() throws JobExecutionException {
        LOG.info("Executing DummyJob");
    }

    @Override
    public String getJobName() {
        return "DummyJob";
    }
}
