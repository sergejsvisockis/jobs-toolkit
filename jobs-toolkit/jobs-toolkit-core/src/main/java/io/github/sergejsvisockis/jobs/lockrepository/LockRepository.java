package io.github.sergejsvisockis.jobs.lockrepository;

import io.github.sergejsvisockis.jobs.LockMetadata;

import java.util.Optional;

/**
 * A distributed lock contract which is supposed to have many implementations depending on ont he use case.
 * Out of the box provides a JDBC (RDBMS), Zookeeper and Redis implementations.
 * Implement this interface in your own distributed lock implementation or use any of the existing implementations.
 */
public interface LockRepository {

    /**
     * Get lock metadata for a job by its name.
     * This is used to manage distributed locks for job executions.
     *
     * @param jobName the name of the job to get lock metadata for.
     * @return an optional lock metadata object if found, otherwise empty.
     */
    Optional<LockMetadata> fetchLockMetadata(String jobName);

    /**
     * Acquire a distributed lock entry in the database for a job by its name.
     * This operation is used to ensure that only one instance of a job can run at a time.
     *
     * @param jobName the name of the job to acquire a lock for.
     * @return the lock metadata object containing lock details.
     */
    LockMetadata acquireLock(String jobName);

    /**
     * Release a distributed lock entry in the database for a job.
     * This operation is used to release the lock after the job execution is completed or failed.
     *
     * @param lockMetadata the lock metadata object containing lock details to be released.
     */
    void releaseLock(LockMetadata lockMetadata);

}
