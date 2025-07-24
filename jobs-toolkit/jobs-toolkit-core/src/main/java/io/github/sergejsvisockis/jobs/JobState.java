package io.github.sergejsvisockis.jobs;

/**
 * Job execution state.
 */
public enum JobState {

    /**
     * Idicates that the job is currently running.
     */
    RUNNING,

    /**
     * Indicates that the job has been completed successfully.
     */
    COMPLETED,

    /**
     * Indicates that the job has failed during execution.
     */
    FAILED

}
