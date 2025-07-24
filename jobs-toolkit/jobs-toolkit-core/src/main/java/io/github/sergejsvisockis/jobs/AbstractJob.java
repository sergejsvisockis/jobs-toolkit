package io.github.sergejsvisockis.jobs;

import io.github.sergejsvisockis.jobs.lockrepository.LockRepository;
import io.github.sergejsvisockis.jobs.repository.JobRepository;

import java.util.Optional;

/**
 * <p>
 * Job API which is supposed to execute some regular operations.
 * Implements a distributed lock to avoid working with the same data at the same time.
 * </p>
 * Could be scheduled or invoked.
 */
public abstract class AbstractJob {

    static final String JOB_NOT_FOUND_MSG = "Job not found: %s";
    static final String JOB_FAILED = "Job with name=%s has failed";

    private JobRepository jobRepository;
    private LockRepository lockRepository;

    /**
     * Default no-args constructor is needed to allow instantiating child classes to use setter-injection
     */
    public AbstractJob() {
    }

    /**
     * Constructor with dependencies.
     *
     * @param jobRepository the job repository
     * @param lockRepository the lock repository
     */
    public AbstractJob(JobRepository jobRepository, LockRepository lockRepository) {
        this.jobRepository = jobRepository;
        this.lockRepository = lockRepository;
    }

    /**
     * Sets the job repository.
     *
     * @param jobRepository the job repository
     */
    public void setJobRepository(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    /**
     * Sets the lock repository.
     *
     * @param lockRepository the lock repository
     */
    public void setLockRepository(LockRepository lockRepository) {
        this.lockRepository = lockRepository;
    }

    /**
     * Makes an actual job execution, acquires lock, releases lock and handles exceptions.
     * Has to be overridden along with all other methods.
     */
    public void run() {
        new Thread(this::doExecute).start();
    }

    void doExecute() {
        String jobName = getJobName();

        Optional<JobDefinition> jobDefinition = jobRepository.fetchJobDefinition(jobName);
        if (jobDefinition.isEmpty()) {
            throw new IllegalStateException(String.format(JOB_NOT_FOUND_MSG, jobName));
        }

        Optional<LockMetadata> lockMetadata = lockRepository.fetchLockMetadata(jobName);

        if (lockMetadata.isEmpty()) {

            LockMetadata lockedJobMetadata = lockRepository.acquireLock(jobDefinition.get().getJobName());

            JobExecution newExecution = jobRepository.captureExecution(jobName);

            try {

                execute();

                jobRepository.completeExecution(newExecution);
            } catch (JobExecutionException e) {

                jobRepository.failExecution(newExecution);

                throw new IllegalStateException(String
                        .format(JOB_FAILED, jobDefinition.get().getJobName()), e);
            } finally {

                lockRepository.releaseLock(lockedJobMetadata);
            }
        }
    }

    /**
     * Executes the job logic.
     *
     * @throws JobExecutionException if the job execution fails
     */
    public abstract void execute() throws JobExecutionException;

    /**
     * Returns the name of the job.
     *
     * @return the job name
     */
    public abstract String getJobName();
}
