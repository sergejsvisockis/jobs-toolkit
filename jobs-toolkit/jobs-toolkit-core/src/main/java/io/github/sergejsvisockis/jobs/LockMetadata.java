package io.github.sergejsvisockis.jobs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Contains a distributed lock metadata.
 */
@JsonDeserialize(builder = LockMetadata.Builder.class)
public final class LockMetadata {

    private final UUID lockId;
    private final String jobName;
    private final LocalDateTime lockedAt;

    private LockMetadata(Builder builder) {
        this.jobName = builder.jobName;
        this.lockId = builder.lockId;
        this.lockedAt = builder.lockedAt;
    }

    /**
     * Gets a lock identifier.
     *
     * @return lock identifier.
     */
    public UUID getLockId() {
        return lockId;
    }

    /**
     * Gets a job name.
     *
     * @return job name.
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Gets the time the job has been locked.
     *
     * @return the time the job has been locked.
     */
    public LocalDateTime getLockedAt() {
        return lockedAt;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    /**
     * Initialised a builder object for this object.
     */
    public static class Builder {
        private UUID lockId;
        private String jobName;
        private LocalDateTime lockedAt;

        /**
         * Sets the lock identifier.
         *
         * @param lockId lock identifier.
         * @return builder instance.
         */
        public Builder withLockId(UUID lockId) {
            this.lockId = lockId;
            return this;
        }

        /**
         * Sets a job name.
         *
         * @param jobName job name
         * @return builder instance.
         */
        public Builder withJobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        /**
         * The time job has been locked.
         *
         * @param lockedAt the time the job has been locked.
         * @return builder instance.
         */
        public Builder withLockedAt(LocalDateTime lockedAt) {
            this.lockedAt = lockedAt;
            return this;
        }

        /**
         * Finalises an object construction.
         *
         * @return an object.
         */
        public LockMetadata build() {
            return new LockMetadata(this);
        }
    }

}
