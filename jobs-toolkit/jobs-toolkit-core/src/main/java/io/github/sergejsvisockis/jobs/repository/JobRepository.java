package io.github.sergejsvisockis.jobs.repository;

import io.github.sergejsvisockis.jobs.JobDefinition;
import io.github.sergejsvisockis.jobs.JobExecution;
import io.github.sergejsvisockis.jobs.LockMetadata;

import java.util.Optional;

/**
 * Data persistence interface to operate with batch jobs and their executions.
 */
public interface JobRepository {

    /**
     * Get a job definition.
     * Job definition on its own is a structure e.g. database table eventually
     * which contains a job name and its description.
     *
     * @param jobName the name of the job to search a definition for
     * @return an optional job definition.
     */
    Optional<JobDefinition> fetchJobDefinition(String jobName);

    /**
     * Capture job execution, its state, and the last time of its execution.
     *
     * @param jobName the name of the job to capture execution for.
     * @return the job execution object containing the state and last execution time.
     */
    JobExecution captureExecution(String jobName);

    /**
     * Save a new job execution from scratch.
     * The core difference is that {@link #captureExecution(String)} performs a check whether
     * the job execution took place before, or not while this operation just saves the new execution entry.
     *
     * @param jobName the name of the job to save a new execution for.
     * @return the newly created job execution object with initial state.
     */
    JobExecution saveNewExecution(String jobName);

    /**
     * Find the last execution of a job by its name.
     *
     * @param jobName the name of the job to search for the last execution.
     * @return an optional job execution object if found, otherwise empty.
     */
    Optional<JobExecution> findLastExecution(String jobName);

    /**
     * Change the job execution state to FAILED.
     *
     * @param jobExecution the job execution object to mark as failed.
     */
    void failExecution(JobExecution jobExecution);

    /**
     * Change the job execution state to COMPLETED.
     *
     * @param jobExecution the job execution object to mark as completed.
     */
    void completeExecution(JobExecution jobExecution);

    /**
     * Update the job execution with new information.
     * By default, it updates the last execution time and the state of the job execution.
     *
     * @param jobExecution the job execution object to update.
     * @return the updated job execution object.
     */
    JobExecution updateExecution(JobExecution jobExecution);
}
