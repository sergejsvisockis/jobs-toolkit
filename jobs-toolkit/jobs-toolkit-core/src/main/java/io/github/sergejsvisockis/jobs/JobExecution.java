package io.github.sergejsvisockis.jobs;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Job execution which is supposed to capture the job execution details.
 * This structure is supposed to represent a database job execution entry.
 */
public class JobExecution {

    private final UUID executionId;
    private final String jobName;
    private LocalDateTime lastRun;
    private JobState state;

    private JobExecution(Builder builder) {
        this.executionId = builder.executionId;
        this.jobName = builder.jobName;
        this.lastRun = builder.lastRun;
        this.state = builder.state;
    }

    /**
     * Get a job execution ID, which is a primary key in the corresponding database table.
     *
     * @return job execution ID.
     */
    public UUID getExecutionId() {
        return executionId;
    }

    /**
     * Job name which state is supposed to be updated.
     *
     * @return job name.
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * The last time the job has been run. No matter which {@link JobState}.
     *
     * @return the last time job has run.
     */
    public LocalDateTime getLastRun() {
        return lastRun;
    }

    /**
     * Sets the last time hte job has run. Compared with all other fields changes with each execution.
     *
     * @param lastRun the last run date and time
     */
    public void setLastRun(LocalDateTime lastRun) {
        this.lastRun = lastRun;
    }

    /**
     * Gets an execution state.
     *
     * @return job execution state.
     */
    public JobState getState() {
        return state;
    }

    /**
     * Sets a job execution state based on the specific state the job is current on.
     *
     * @param state job execution state.
     */
    public void setState(JobState state) {
        this.state = state;
    }

    /**
     * Constructs a job execution object.
     */
    public static class Builder {
        private UUID executionId;
        private String jobName;
        private LocalDateTime lastRun;
        private JobState state;

        /**
         * Sets a job execution identifier.
         *
         * @param executionId job execution identifier.
         * @return builder instance.
         */
        public Builder withExecutionId(UUID executionId) {
            this.executionId = executionId;
            return this;
        }

        /**
         * Sets a job name.
         *
         * @param jobName the job name.
         * @return builder instance.
         */
        public Builder withJobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        /**
         * Sets the last run job has been run.
         * Mostly the very initial stage once the state is being set for the first time.
         *
         * @param lastRun the last time job has been run.
         * @return builder instance.
         */
        public Builder withLastRun(LocalDateTime lastRun) {
            this.lastRun = lastRun;
            return this;
        }

        /**
         * The job execution state.
         * Mostly the very initial stage once the state is being set for the first time.
         *
         * @param state job execution state.
         * @return builder instance.
         */
        public Builder withState(JobState state) {
            this.state = state;
            return this;
        }

        /**
         * Finalised a construction of the actual object.
         *
         * @return finalised job execution objet.
         */
        public JobExecution build() {
            return new JobExecution(this);
        }
    }
}
