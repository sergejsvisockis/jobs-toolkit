package io.github.sergejsvisockis.jobs.demo.mongo;

import io.github.sergejsvisockis.jobs.AbstractJob;
import io.github.sergejsvisockis.jobs.JobExecutionException;
import io.github.sergejsvisockis.jobs.lockrepository.LockRepository;
import io.github.sergejsvisockis.jobs.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static java.util.concurrent.TimeUnit.SECONDS;

@Component
public class ExperimentJob extends AbstractJob {

    private static final Logger LOG = LoggerFactory.getLogger(ExperimentJob.class);

    public ExperimentJob(JobRepository jobRepository, LockRepository lockRepository) {
        super(jobRepository, lockRepository);
    }

    @Scheduled(fixedRate = 10, timeUnit = SECONDS)
    @Override
    public void run() {
        super.run();
    }

    @Override
    public void execute() throws JobExecutionException {
        LOG.info("Doing some job");
    }

    @Override
    public String getJobName() {
        return "ExperimentJob";
    }
}
