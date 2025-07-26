package io.github.sergejsvisockis.jobs.demo;

import io.github.sergejsvisockis.jobs.AbstractRetryableJob;
import io.github.sergejsvisockis.jobs.JobExecutionException;
import io.github.sergejsvisockis.jobs.lockrepository.LockRepository;
import io.github.sergejsvisockis.jobs.repository.JobRepository;
import io.github.sergejsvisockis.jobs.retry.repository.JobRetryHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static java.util.concurrent.TimeUnit.MINUTES;

@Component
public class ExperimentJob extends AbstractRetryableJob {

    private static final Logger LOG = LoggerFactory.getLogger(ExperimentJob.class);

    public ExperimentJob(@Value("${jobs.retry.delay-millis}") Long retryDelayMillis,
                         @Value("${jobs.retry.max-attempts}") Integer maxRetries,
                         JobRepository jobRepository,
                         LockRepository lockRepository,
                         JobRetryHistoryRepository jobRetryHistoryRepository) {
        super(retryDelayMillis, maxRetries, jobRepository,
                lockRepository, jobRetryHistoryRepository);
    }


    @Scheduled(fixedRate = 30, timeUnit = MINUTES)
    @Override
    public void run() {
        super.run();
    }

    @Override
    public void execute() throws JobExecutionException {
        LOG.info("Doing some job");
        throw new JobExecutionException("Simulated job failure");
    }

    @Override
    public String getJobName() {
        return "ExperimentJob";
    }
}
