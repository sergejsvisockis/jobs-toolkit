package io.github.sergejsvisockis.jobs;

/**
 * Job definition is a simple structure which 1:1 represents a corresponding database entry.
 * The key role if this object is to hold a very simple job metadata like its name and description.
 */
public final class JobDefinition {

    private final String jobName;
    private final String jobDescription;

    private JobDefinition(Builder builder) {
        this.jobName = builder.jobName;
        this.jobDescription = builder.jobDescription;
    }

    /**
     * Get a job name.
     *
     * @return job name.
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Get a job description.
     * Supposed to be a free form job description.
     *
     * @return job description.
     */
    public String getJobDescription() {
        return jobDescription;
    }

    /**
     * Constructs a job definition object
     */
    public static class Builder {
        private String jobName;
        private String jobDescription;

        /**
         * Sets a job name.
         *
         * @param jobName job name.
         * @return builder.
         */
        public Builder withJobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        /**
         * Sets a job description.
         *
         * @param jobDescription job description.
         * @return builder.
         */
        public Builder withJobDescription(String jobDescription) {
            this.jobDescription = jobDescription;
            return this;
        }

        /**
         * Finalises an object construction.
         *
         * @return constructed object.
         */
        public JobDefinition build() {
            return new JobDefinition(this);
        }
    }

}
