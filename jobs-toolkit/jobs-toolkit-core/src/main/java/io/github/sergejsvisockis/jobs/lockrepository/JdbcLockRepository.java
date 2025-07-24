package io.github.sergejsvisockis.jobs.lockrepository;

import io.github.sergejsvisockis.jobs.LockMetadata;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

public class JdbcLockRepository implements LockRepository {

    private static final String QUERY_FIND_LOCK_METADATA = "SELECT l.lock_id, l.job_name, l.locked_at FROM lock_metadata l WHERE job_name = ?";
    private static final String QUERY_ACQUIRE_LOCK = "INSERT INTO lock_metadata(lock_id, job_name, locked_at) VALUES (?, ?, ?)";
    private static final String QUERY_RELEASE_JOB_LOCK = "DELETE FROM lock_metadata WHERE lock_id = ?";

    static final String FAILED_TO_GET_LOCK_METADATA_MSG = "Failed to get a lock metadata for a job with name=%s";
    static final String FAILED_TO_ACQUIRE_LOCK_MSG = "Failed to acquire a lock for a job with name=%s";
    static final String FAILED_TO_RELEASE_LOCK_MSG = "Failed to release a lock for a job with name=%s locked at =%s with a lock=%s";

    private final DataSource dataSource;

    public JdbcLockRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<LockMetadata> fetchLockMetadata(String jobName) {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(QUERY_FIND_LOCK_METADATA);) {

            pstmt.setString(1, jobName);
            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    String lockId = rs.getString(1);
                    String name = rs.getString(2);
                    Timestamp lockedAt = rs.getTimestamp(3);
                    return Optional.of(new LockMetadata.Builder()
                            .withLockId(UUID.fromString(lockId))
                            .withJobName(name)
                            .withLockedAt(lockedAt.toLocalDateTime())
                            .build());
                }

            }

            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException(String.format(FAILED_TO_GET_LOCK_METADATA_MSG, jobName), e);
        }
    }

    @Override
    public LockMetadata acquireLock(String jobName) {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(QUERY_ACQUIRE_LOCK)) {

            UUID lockId = UUID.randomUUID();
            Timestamp lockedAt = new Timestamp(System.currentTimeMillis());

            pstmt.setString(1, lockId.toString());
            pstmt.setString(2, jobName);
            pstmt.setTimestamp(3, lockedAt);

            pstmt.executeUpdate();

            return new LockMetadata.Builder()
                    .withJobName(jobName)
                    .withLockId(lockId)
                    .withLockedAt(lockedAt.toLocalDateTime())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(String.format(FAILED_TO_ACQUIRE_LOCK_MSG, jobName), e);
        }

    }

    @Override
    public void releaseLock(LockMetadata lockMetadata) {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(QUERY_RELEASE_JOB_LOCK)) {

            pstmt.setString(1, lockMetadata.getLockId().toString());
            pstmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(String.format(FAILED_TO_RELEASE_LOCK_MSG,
                    lockMetadata.getJobName(),
                    lockMetadata.getLockedAt(),
                    lockMetadata.getLockId()), e);
        }

    }
}
