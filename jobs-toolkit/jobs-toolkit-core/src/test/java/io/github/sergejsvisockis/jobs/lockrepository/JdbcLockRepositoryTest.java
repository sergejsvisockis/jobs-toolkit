package io.github.sergejsvisockis.jobs.lockrepository;

import io.github.sergejsvisockis.jobs.LockMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static io.github.sergejsvisockis.jobs.lockrepository.JdbcLockRepository.FAILED_TO_ACQUIRE_LOCK_MSG;
import static io.github.sergejsvisockis.jobs.lockrepository.JdbcLockRepository.FAILED_TO_GET_LOCK_METADATA_MSG;
import static io.github.sergejsvisockis.jobs.lockrepository.JdbcLockRepository.FAILED_TO_RELEASE_LOCK_MSG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JdbcLockRepositoryTest {

    @Mock
    private DataSource dataSource;

    @InjectMocks
    private JdbcLockRepository lockRepository;

    @Test
    void shouldFetchLockMetadata() throws SQLException {
        //given
        final String jobName = "TestJob";
        final UUID lockId = UUID.randomUUID();
        final LocalDateTime lockedAt = LocalDateTime.now();

        Connection connectionMock = mock(Connection.class);
        PreparedStatement pstmtMock = mock(PreparedStatement.class);
        ResultSet rsMock = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement("SELECT l.lock_id, l.job_name, l.locked_at FROM lock_metadata l WHERE job_name = ?"))
                .thenReturn(pstmtMock);
        when(pstmtMock.executeQuery()).thenReturn(rsMock);
        when(rsMock.next()).thenReturn(true);
        when(rsMock.getString(1)).thenReturn(lockId.toString());
        when(rsMock.getString(2)).thenReturn(jobName);
        when(rsMock.getTimestamp(3)).thenReturn(Timestamp.valueOf(lockedAt));

        //when
        Optional<LockMetadata> lockMetadata = lockRepository.fetchLockMetadata(jobName);

        //then
        assertTrue(lockMetadata.isPresent());
        assertEquals(lockId, lockMetadata.get().getLockId());
        assertEquals(jobName, lockMetadata.get().getJobName());
        assertEquals(lockedAt, lockMetadata.get().getLockedAt());

        verify(dataSource).getConnection();
        verify(connectionMock).prepareStatement("SELECT l.lock_id, l.job_name, l.locked_at FROM lock_metadata l WHERE job_name = ?");
        verify(pstmtMock).setString(1, jobName);
        verify(pstmtMock).executeQuery();
        verify(rsMock).next();
        verify(rsMock).getString(1);
        verify(rsMock).getString(2);
        verify(rsMock).getTimestamp(3);
        verify(rsMock).close();
        verify(pstmtMock).close();
        verify(connectionMock).close();
    }

    @Test
    void shouldReturnEmptyWhenNoLockMetadata() throws SQLException {
        //given
        final String jobName = "TestJob";

        Connection connectionMock = mock(Connection.class);
        PreparedStatement pstmtMock = mock(PreparedStatement.class);
        ResultSet rsMock = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement("SELECT l.lock_id, l.job_name, l.locked_at FROM lock_metadata l WHERE job_name = ?"))
                .thenReturn(pstmtMock);
        when(pstmtMock.executeQuery()).thenReturn(rsMock);
        when(rsMock.next()).thenReturn(false);

        //when
        Optional<LockMetadata> lockMetadata = lockRepository.fetchLockMetadata(jobName);

        //then
        assertTrue(lockMetadata.isEmpty());

        verify(dataSource).getConnection();
        verify(connectionMock).prepareStatement("SELECT l.lock_id, l.job_name, l.locked_at FROM lock_metadata l WHERE job_name = ?");
        verify(pstmtMock).setString(1, jobName);
        verify(pstmtMock).executeQuery();
        verify(rsMock).next();
        verify(rsMock, never()).getString(anyInt());
        verify(rsMock, never()).getTimestamp(anyInt());
        verify(rsMock).close();
        verify(pstmtMock).close();
        verify(connectionMock).close();
    }

    @Test
    void shouldFailWithExceptionWhenGettingLockMetadata() throws SQLException {
        //given
        final String jobName = "TestJob";
        Connection connectionMock = mock(Connection.class);

        when(dataSource.getConnection()).thenThrow(new SQLException());

        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> lockRepository.fetchLockMetadata(jobName));

        //then
        assertEquals(String.format(FAILED_TO_GET_LOCK_METADATA_MSG, jobName), exception.getMessage());
        verify(dataSource).getConnection();
        verify(connectionMock, never()).prepareStatement(anyString());
    }

    @Test
    void shouldAcquireLock() throws SQLException {
        //given
        final String jobName = "TestJob";

        Connection connectionMock = mock(Connection.class);
        PreparedStatement pstmtMock = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement("INSERT INTO lock_metadata(lock_id, job_name, locked_at) VALUES (?, ?, ?)"))
                .thenReturn(pstmtMock);

        //when
        LockMetadata lockMetadata = lockRepository.acquireLock(jobName);

        //then
        assertNotNull(lockMetadata.getLockId());
        assertEquals(jobName, lockMetadata.getJobName());
        assertNotNull(lockMetadata.getLockedAt());

        verify(dataSource).getConnection();
        verify(connectionMock).prepareStatement("INSERT INTO lock_metadata(lock_id, job_name, locked_at) VALUES (?, ?, ?)");
        verify(pstmtMock).setString(eq(1), anyString());
        verify(pstmtMock).setString(2, jobName);
        verify(pstmtMock).setTimestamp(eq(3), any(Timestamp.class));
        verify(pstmtMock).executeUpdate();
        verify(pstmtMock).close();
        verify(connectionMock).close();
    }

    @Test
    void shouldFailWithExceptionWhenAcquiringLock() throws SQLException {
        //given
        final String jobName = "TestJob";
        Connection connectionMock = mock(Connection.class);

        when(dataSource.getConnection()).thenThrow(new SQLException());

        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> lockRepository.acquireLock(jobName));

        //then
        assertEquals(String.format(FAILED_TO_ACQUIRE_LOCK_MSG, jobName), exception.getMessage());
        verify(dataSource).getConnection();
        verify(connectionMock, never()).prepareStatement(anyString());
    }

    @Test
    void shouldReleaseLock() throws SQLException {
        //given
        final String jobName = "TestJob";
        final UUID lockId = UUID.randomUUID();
        final LocalDateTime lockedAt = LocalDateTime.now();

        LockMetadata lockMetadata = new LockMetadata.Builder()
                .withLockId(lockId)
                .withJobName(jobName)
                .withLockedAt(lockedAt)
                .build();

        Connection connectionMock = mock(Connection.class);
        PreparedStatement pstmtMock = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement("DELETE FROM lock_metadata WHERE lock_id = ?"))
                .thenReturn(pstmtMock);

        //when
        lockRepository.releaseLock(lockMetadata);

        //then
        verify(dataSource).getConnection();
        verify(connectionMock).prepareStatement("DELETE FROM lock_metadata WHERE lock_id = ?");
        verify(pstmtMock).setString(1, lockId.toString());
        verify(pstmtMock).executeUpdate();
        verify(pstmtMock).close();
        verify(connectionMock).close();
    }

    @Test
    void shouldFailWithExceptionWhenReleasingLock() throws SQLException {
        //given
        final String jobName = "TestJob";
        final UUID lockId = UUID.randomUUID();
        final LocalDateTime lockedAt = LocalDateTime.now();

        LockMetadata lockMetadata = new LockMetadata.Builder()
                .withLockId(lockId)
                .withJobName(jobName)
                .withLockedAt(lockedAt)
                .build();

        Connection connectionMock = mock(Connection.class);

        when(dataSource.getConnection()).thenThrow(new SQLException());

        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> lockRepository.releaseLock(lockMetadata));

        //then
        assertEquals(String.format(FAILED_TO_RELEASE_LOCK_MSG, jobName, lockedAt, lockId), exception.getMessage());
        verify(dataSource).getConnection();
        verify(connectionMock, never()).prepareStatement(anyString());
    }
}