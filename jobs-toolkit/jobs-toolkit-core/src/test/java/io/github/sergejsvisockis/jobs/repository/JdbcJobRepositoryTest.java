package io.github.sergejsvisockis.jobs.repository;

import io.github.sergejsvisockis.jobs.JobDefinition;
import io.github.sergejsvisockis.jobs.JobExecution;
import io.github.sergejsvisockis.jobs.JobState;
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

import static io.github.sergejsvisockis.jobs.repository.JdbcJobRepository.FAILED_TO_CAPTURE_EXECUTION_MSG;
import static io.github.sergejsvisockis.jobs.repository.JdbcJobRepository.FAILED_TO_COMPLETE_EXECUTION_MSG;
import static io.github.sergejsvisockis.jobs.repository.JdbcJobRepository.FAILED_TO_FAIL_EXECUTION_MSG;
import static io.github.sergejsvisockis.jobs.repository.JdbcJobRepository.FAILED_TO_GET_JOB_DEFINITION_MSG;
import static io.github.sergejsvisockis.jobs.repository.JdbcJobRepository.FAILED_TO_GET_LAST_EXECUTION_MSG;
import static io.github.sergejsvisockis.jobs.repository.JdbcJobRepository.FAILED_TO_UPDATE_EXECUTION;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JdbcJobRepositoryTest {

    @Mock
    private DataSource dataSource;

    @InjectMocks
    private JdbcJobRepository jobRepository;

    @Test
    void shouldFetchJobDefinition() throws SQLException {
        //given
        final String jobName = "TestJob";
        Connection connectionMock = mock(Connection.class);
        PreparedStatement pstmtMock = mock(PreparedStatement.class);
        ResultSet rsMock = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement("SELECT j.job_name, j.job_description FROM job_definition j WHERE job_name = ?"))
                .thenReturn(pstmtMock);
        when(pstmtMock.executeQuery()).thenReturn(rsMock);
        when(rsMock.next()).thenReturn(true);
        when(rsMock.getString(1)).thenReturn(jobName);
        when(rsMock.getString(2)).thenReturn("Test job description");

        //when
        Optional<JobDefinition> jobDefinition = jobRepository.fetchJobDefinition(jobName);

        //then
        assertEquals(jobName, jobDefinition.get().getJobName());
        assertEquals("Test job description", jobDefinition.get().getJobDescription());
        verify(dataSource).getConnection();
        verify(connectionMock).prepareStatement("SELECT j.job_name, j.job_description FROM job_definition j WHERE job_name = ?");
        verify(pstmtMock).setString(1, jobName);
        verify(pstmtMock).executeQuery();
        verify(rsMock).next();
        verify(rsMock).getString(1);
        verify(rsMock).getString(2);
        verify(rsMock).close();
        verify(pstmtMock).close();
        verify(connectionMock).close();
    }

    @Test
    void shouldReturnEmptyResultSetWhenQueryingJobDefinition() throws SQLException {
        //given
        final String jobName = "TestJob";
        Connection connectionMock = mock(Connection.class);
        PreparedStatement pstmtMock = mock(PreparedStatement.class);
        ResultSet rsMock = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement("SELECT j.job_name, j.job_description FROM job_definition j WHERE job_name = ?"))
                .thenReturn(pstmtMock);
        when(pstmtMock.executeQuery()).thenReturn(rsMock);
        when(rsMock.next()).thenReturn(false);

        //when
        Optional<JobDefinition> jobDefinition = jobRepository.fetchJobDefinition(jobName);

        //then
        assertTrue(jobDefinition.isEmpty());
        verify(dataSource).getConnection();
        verify(connectionMock).prepareStatement("SELECT j.job_name, j.job_description FROM job_definition j WHERE job_name = ?");
        verify(pstmtMock).setString(1, jobName);
        verify(pstmtMock).executeQuery();
        verify(rsMock).next();
        verify(rsMock, never()).getString(1);
        verify(rsMock, never()).getString(2);
        verify(rsMock).close();
        verify(pstmtMock).close();
        verify(connectionMock).close();
    }

    @Test
    void shouldFailWithExceptionWhileFetchingJobDefinition() throws SQLException {
        //given
        final String jobName = "TestJob";
        Connection connectionMock = mock(Connection.class);
        PreparedStatement pstmtMock = mock(PreparedStatement.class);
        ResultSet rsMock = mock(ResultSet.class);

        when(dataSource.getConnection()).thenThrow(new SQLException());

        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jobRepository.fetchJobDefinition(jobName));

        //then
        assertEquals(String.format(FAILED_TO_GET_JOB_DEFINITION_MSG, jobName), exception.getMessage());
        verify(dataSource).getConnection();
        verify(connectionMock, never()).prepareStatement("SELECT j.job_name, j.job_description FROM job_definition j WHERE job_name = ?");
        verify(pstmtMock, never()).setString(1, jobName);
        verify(pstmtMock, never()).executeQuery();
        verify(rsMock, never()).next();
        verify(rsMock, never()).getString(1);
        verify(rsMock, never()).getString(2);
        verify(rsMock, never()).close();
        verify(pstmtMock, never()).close();
        verify(connectionMock, never()).close();
    }

    @Test
    void shouldSaveNewExecution() throws SQLException {
        //given
        final String jobName = "TestJob";
        Connection connectionMock = mock(Connection.class);
        PreparedStatement pstmtMock = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement("INSERT INTO job_execution(execution_id, job_name, last_run, state) VALUES (?, ?, ?, ?)"))
                .thenReturn(pstmtMock);

        //when
        JobExecution jobExecution = jobRepository.saveNewExecution(jobName);

        //then
        assertNotNull(jobExecution.getExecutionId());
        assertEquals(jobName, jobExecution.getJobName());
        assertNotNull(jobExecution.getLastRun());
        assertEquals(JobState.RUNNING, jobExecution.getState());
        verify(dataSource).getConnection();
        verify(connectionMock).prepareStatement("INSERT INTO job_execution(execution_id, job_name, last_run, state) VALUES (?, ?, ?, ?)");
        verify(pstmtMock).setString(eq(1), anyString());
        verify(pstmtMock).setString(2, jobName);
        verify(pstmtMock).setTimestamp(eq(3), any(Timestamp.class));
        verify(pstmtMock).setString(4, JobState.RUNNING.name());
        verify(pstmtMock).executeUpdate();
        verify(pstmtMock).close();
        verify(connectionMock).close();
    }

    @Test
    void shouldFailWithExceptionWhenSavingANewExecution() throws SQLException {
        //given
        final String jobName = "TestJob";
        Connection connectionMock = mock(Connection.class);
        PreparedStatement pstmtMock = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenThrow(new SQLException());

        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jobRepository.saveNewExecution(jobName));

        //given
        assertEquals(String.format(FAILED_TO_CAPTURE_EXECUTION_MSG, jobName), exception.getMessage());
        verify(dataSource).getConnection();
        verify(connectionMock, never()).prepareStatement("INSERT INTO job_execution(execution_id, job_name, last_run, state) VALUES (?, ?, ?, ?)");
        verify(pstmtMock, never()).setString(eq(1), anyString());
        verify(pstmtMock, never()).setString(2, jobName);
        verify(pstmtMock, never()).setTimestamp(eq(3), any(Timestamp.class));
        verify(pstmtMock, never()).setString(4, JobState.RUNNING.name());
        verify(pstmtMock, never()).executeUpdate();
        verify(pstmtMock, never()).close();
        verify(connectionMock, never()).close();
    }

    @Test
    void shouldUpdateExecution() throws SQLException {
        //given
        final JobState jobState = JobState.RUNNING;
        final LocalDateTime lastRun = LocalDateTime.now();
        final UUID executionId = UUID.fromString("7220bd97-bf66-4231-8465-c28ea0752e66");

        final JobExecution execution = new JobExecution.Builder()
                .withState(jobState)
                .withLastRun(lastRun)
                .withExecutionId(executionId)
                .build();

        Connection connectionMock = mock(Connection.class);
        PreparedStatement pstmtMock = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement("UPDATE job_execution SET state = ?, last_run = ? WHERE execution_id = ?"))
                .thenReturn(pstmtMock);

        //when
        JobExecution jobExecution = jobRepository.updateExecution(execution);

        //then
        assertEquals(jobState, jobExecution.getState());
        assertEquals(lastRun, jobExecution.getLastRun());
        assertEquals(executionId, jobExecution.getExecutionId());
        verify(dataSource).getConnection();
        verify(connectionMock).prepareStatement("UPDATE job_execution SET state = ?, last_run = ? WHERE execution_id = ?");
        verify(pstmtMock).setString(1, jobState.name());
        verify(pstmtMock).setTimestamp(2, Timestamp.valueOf(lastRun));
        verify(pstmtMock).setString(3, executionId.toString());
        verify(pstmtMock).executeUpdate();
        verify(pstmtMock).close();
        verify(connectionMock).close();
    }

    @Test
    void shouldFailWithAnExceptionWhileUpdatingAnExecution() throws SQLException {
        //given
        final String jobName = "TestJob";
        final UUID executionId = UUID.fromString("7220bd97-bf66-4231-8465-c28ea0752e66");

        JobExecution jobExecutionMock = mock(JobExecution.class);
        when(jobExecutionMock.getJobName()).thenReturn(jobName);
        when(jobExecutionMock.getExecutionId()).thenReturn(executionId);

        Connection connectionMock = mock(Connection.class);
        PreparedStatement pstmtMock = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenThrow(new SQLException());

        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jobRepository.updateExecution(jobExecutionMock));

        //then
        assertEquals(String.format(FAILED_TO_UPDATE_EXECUTION, jobName, executionId), exception.getMessage());
        verify(dataSource).getConnection();
        verify(connectionMock, never()).prepareStatement("UPDATE job_execution SET state = ?, last_run = ? WHERE execution_id = ?");
        verify(pstmtMock, never()).setString(eq(1), anyString());
        verify(pstmtMock, never()).setTimestamp(eq(2), any(Timestamp.class));
        verify(pstmtMock, never()).setString(3, executionId.toString());
        verify(pstmtMock, never()).executeUpdate();
        verify(pstmtMock, never()).close();
        verify(connectionMock, never()).close();
    }

    @Test
    void shouldCaptureExecutionWhenNoExistingExecution() throws SQLException {
        //given
        final String jobName = "TestJob";
        Connection connectionMock = mock(Connection.class);
        PreparedStatement pstmtFindMock = mock(PreparedStatement.class);
        PreparedStatement pstmtSaveMock = mock(PreparedStatement.class);
        ResultSet rsMock = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement("SELECT j.execution_id, j.job_name, j.last_run, j.state FROM job_execution j WHERE job_name = ?"))
                .thenReturn(pstmtFindMock);
        when(pstmtFindMock.executeQuery()).thenReturn(rsMock);
        when(rsMock.next()).thenReturn(false);

        when(connectionMock.prepareStatement("INSERT INTO job_execution(execution_id, job_name, last_run, state) VALUES (?, ?, ?, ?)"))
                .thenReturn(pstmtSaveMock);

        //when
        JobExecution jobExecution = jobRepository.captureExecution(jobName);

        //then
        assertNotNull(jobExecution.getExecutionId());
        assertEquals(jobName, jobExecution.getJobName());
        assertNotNull(jobExecution.getLastRun());
        assertEquals(JobState.RUNNING, jobExecution.getState());

        verify(dataSource, times(2)).getConnection();
        verify(connectionMock).prepareStatement("SELECT j.execution_id, j.job_name, j.last_run, j.state FROM job_execution j WHERE job_name = ?");
        verify(pstmtFindMock).setString(1, jobName);
        verify(pstmtFindMock).executeQuery();
        verify(rsMock).next();
        verify(rsMock).close();
        verify(pstmtFindMock).close();

        verify(connectionMock).prepareStatement("INSERT INTO job_execution(execution_id, job_name, last_run, state) VALUES (?, ?, ?, ?)");
        verify(pstmtSaveMock).setString(eq(1), anyString());
        verify(pstmtSaveMock).setString(2, jobName);
        verify(pstmtSaveMock).setTimestamp(eq(3), any(Timestamp.class));
        verify(pstmtSaveMock).setString(4, JobState.RUNNING.name());
        verify(pstmtSaveMock).executeUpdate();
        verify(pstmtSaveMock).close();
        verify(connectionMock, times(2)).close();
    }

    @Test
    void shouldCaptureExecutionWhenExistingExecution() throws SQLException {
        //given
        final String jobName = "TestJob";
        final UUID executionId = UUID.randomUUID();
        final Timestamp lastRun = new Timestamp(System.currentTimeMillis() - 10000);

        Connection connectionMock = mock(Connection.class);
        PreparedStatement pstmtFindMock = mock(PreparedStatement.class);
        PreparedStatement pstmtUpdateMock = mock(PreparedStatement.class);
        ResultSet rsMock = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement("SELECT j.execution_id, j.job_name, j.last_run, j.state FROM job_execution j WHERE job_name = ?"))
                .thenReturn(pstmtFindMock);
        when(pstmtFindMock.executeQuery()).thenReturn(rsMock);
        when(rsMock.next()).thenReturn(true);
        when(rsMock.getString(1)).thenReturn(executionId.toString());
        when(rsMock.getString(2)).thenReturn(jobName);
        when(rsMock.getTimestamp(3)).thenReturn(lastRun);
        when(rsMock.getString(4)).thenReturn(JobState.COMPLETED.name());

        when(connectionMock.prepareStatement("UPDATE job_execution SET state = ?, last_run = ? WHERE execution_id = ?"))
                .thenReturn(pstmtUpdateMock);

        //when
        JobExecution jobExecution = jobRepository.captureExecution(jobName);

        //then
        assertEquals(executionId, jobExecution.getExecutionId());
        assertEquals(jobName, jobExecution.getJobName());
        assertNotNull(jobExecution.getLastRun());
        assertEquals(JobState.RUNNING, jobExecution.getState());

        verify(dataSource, times(2)).getConnection();
        verify(connectionMock).prepareStatement("SELECT j.execution_id, j.job_name, j.last_run, j.state FROM job_execution j WHERE job_name = ?");
        verify(pstmtFindMock).setString(1, jobName);
        verify(pstmtFindMock).executeQuery();
        verify(rsMock).next();
        verify(rsMock).getString(1);
        verify(rsMock).getString(2);
        verify(rsMock).getTimestamp(3);
        verify(rsMock).getString(4);
        verify(rsMock).close();
        verify(pstmtFindMock).close();

        verify(connectionMock).prepareStatement("UPDATE job_execution SET state = ?, last_run = ? WHERE execution_id = ?");
        verify(pstmtUpdateMock).setString(1, JobState.RUNNING.name());
        verify(pstmtUpdateMock).setTimestamp(eq(2), any(Timestamp.class));
        verify(pstmtUpdateMock).setString(3, executionId.toString());
        verify(pstmtUpdateMock).executeUpdate();
        verify(pstmtUpdateMock).close();
        verify(connectionMock, times(2)).close();
    }

    @Test
    void shouldFailWithExceptionWhenCapturingExecution() throws SQLException {
        //given
        final String jobName = "TestJob";
        Connection connectionMock = mock(Connection.class);

        when(dataSource.getConnection()).thenThrow(new SQLException());

        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jobRepository.captureExecution(jobName));

        //then
        assertEquals(String.format(FAILED_TO_GET_LAST_EXECUTION_MSG, jobName), exception.getMessage());
        verify(dataSource).getConnection();
        verify(connectionMock, never()).prepareStatement(anyString());
    }

    @Test
    void shouldFindLastExecution() throws SQLException {
        //given
        final String jobName = "TestJob";
        final UUID executionId = UUID.randomUUID();
        final Timestamp lastRun = new Timestamp(System.currentTimeMillis());

        Connection connectionMock = mock(Connection.class);
        PreparedStatement pstmtMock = mock(PreparedStatement.class);
        ResultSet rsMock = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement("SELECT j.execution_id, j.job_name, j.last_run, j.state FROM job_execution j WHERE job_name = ?"))
                .thenReturn(pstmtMock);
        when(pstmtMock.executeQuery()).thenReturn(rsMock);
        when(rsMock.next()).thenReturn(true);
        when(rsMock.getString(1)).thenReturn(executionId.toString());
        when(rsMock.getString(2)).thenReturn(jobName);
        when(rsMock.getTimestamp(3)).thenReturn(lastRun);
        when(rsMock.getString(4)).thenReturn(JobState.COMPLETED.name());

        //when
        Optional<JobExecution> jobExecution = jobRepository.findLastExecution(jobName);

        //then
        assertTrue(jobExecution.isPresent());
        assertEquals(executionId, jobExecution.get().getExecutionId());
        assertEquals(jobName, jobExecution.get().getJobName());
        assertEquals(lastRun.toLocalDateTime(), jobExecution.get().getLastRun());
        assertEquals(JobState.COMPLETED, jobExecution.get().getState());

        verify(dataSource).getConnection();
        verify(connectionMock).prepareStatement("SELECT j.execution_id, j.job_name, j.last_run, j.state FROM job_execution j WHERE job_name = ?");
        verify(pstmtMock).setString(1, jobName);
        verify(pstmtMock).executeQuery();
        verify(rsMock).next();
        verify(rsMock).getString(1);
        verify(rsMock).getString(2);
        verify(rsMock).getTimestamp(3);
        verify(rsMock).getString(4);
        verify(rsMock).close();
        verify(pstmtMock).close();
        verify(connectionMock).close();
    }

    @Test
    void shouldReturnEmptyWhenNoLastExecution() throws SQLException {
        //given
        final String jobName = "TestJob";

        Connection connectionMock = mock(Connection.class);
        PreparedStatement pstmtMock = mock(PreparedStatement.class);
        ResultSet rsMock = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement("SELECT j.execution_id, j.job_name, j.last_run, j.state FROM job_execution j WHERE job_name = ?"))
                .thenReturn(pstmtMock);
        when(pstmtMock.executeQuery()).thenReturn(rsMock);
        when(rsMock.next()).thenReturn(false);

        //when
        Optional<JobExecution> jobExecution = jobRepository.findLastExecution(jobName);

        //then
        assertTrue(jobExecution.isEmpty());

        verify(dataSource).getConnection();
        verify(connectionMock).prepareStatement("SELECT j.execution_id, j.job_name, j.last_run, j.state FROM job_execution j WHERE job_name = ?");
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
    void shouldFailWithExceptionWhenFindingLastExecution() throws SQLException {
        //given
        final String jobName = "TestJob";
        Connection connectionMock = mock(Connection.class);

        when(dataSource.getConnection()).thenThrow(new SQLException());

        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jobRepository.findLastExecution(jobName));

        //then
        assertEquals(String.format(FAILED_TO_GET_LAST_EXECUTION_MSG, jobName), exception.getMessage());
        verify(dataSource).getConnection();
        verify(connectionMock, never()).prepareStatement(anyString());
    }

    @Test
    void shouldFailExecution() throws SQLException {
        //given
        final String jobName = "TestJob";
        final UUID executionId = UUID.randomUUID();

        JobExecution jobExecution = new JobExecution.Builder()
                .withExecutionId(executionId)
                .withJobName(jobName)
                .withState(JobState.RUNNING)
                .build();

        Connection connectionMock = mock(Connection.class);
        PreparedStatement pstmtMock = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement("UPDATE job_execution SET state = ?, last_run = ? WHERE execution_id = ?"))
                .thenReturn(pstmtMock);

        //when
        jobRepository.failExecution(jobExecution);

        //then
        verify(dataSource).getConnection();
        verify(connectionMock).prepareStatement("UPDATE job_execution SET state = ?, last_run = ? WHERE execution_id = ?");
        verify(pstmtMock).setString(1, JobState.FAILED.name());
        verify(pstmtMock).setTimestamp(eq(2), any(Timestamp.class));
        verify(pstmtMock).setString(3, executionId.toString());
        verify(pstmtMock).executeUpdate();
        verify(pstmtMock).close();
        verify(connectionMock).close();
    }

    @Test
    void shouldFailWithExceptionWhenFailingExecution() throws SQLException {
        //given
        final String jobName = "TestJob";
        final UUID executionId = UUID.randomUUID();

        JobExecution jobExecution = new JobExecution.Builder()
                .withExecutionId(executionId)
                .withJobName(jobName)
                .withState(JobState.RUNNING)
                .build();

        Connection connectionMock = mock(Connection.class);
        PreparedStatement pstmtMock = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenThrow(new SQLException());

        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jobRepository.failExecution(jobExecution));

        //then
        assertEquals(String.format(FAILED_TO_FAIL_EXECUTION_MSG, jobName, executionId), exception.getMessage());
        verify(dataSource).getConnection();
        verify(connectionMock, never()).prepareStatement(anyString());
        verify(pstmtMock, never()).executeUpdate();
    }

    @Test
    void shouldCompleteExecution() throws SQLException {
        //given
        final String jobName = "TestJob";
        final UUID executionId = UUID.randomUUID();

        JobExecution jobExecution = new JobExecution.Builder()
                .withExecutionId(executionId)
                .withJobName(jobName)
                .withState(JobState.RUNNING)
                .build();

        Connection connectionMock = mock(Connection.class);
        PreparedStatement pstmtMock = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement("UPDATE job_execution SET state = ?, last_run = ? WHERE execution_id = ?"))
                .thenReturn(pstmtMock);

        //when
        jobRepository.completeExecution(jobExecution);

        //then
        verify(dataSource).getConnection();
        verify(connectionMock).prepareStatement("UPDATE job_execution SET state = ?, last_run = ? WHERE execution_id = ?");
        verify(pstmtMock).setString(1, JobState.COMPLETED.name());
        verify(pstmtMock).setTimestamp(eq(2), any(Timestamp.class));
        verify(pstmtMock).setString(3, executionId.toString());
        verify(pstmtMock).executeUpdate();
        verify(pstmtMock).close();
        verify(connectionMock).close();
    }

    @Test
    void shouldNotCompleteExecutionWhenStatusIsFailed() throws SQLException {
        //given
        final String jobName = "TestJob";
        final UUID executionId = UUID.randomUUID();

        JobExecution jobExecution = new JobExecution.Builder()
                .withExecutionId(executionId)
                .withJobName(jobName)
                .withState(JobState.FAILED)
                .build();

        Connection connectionMock = mock(Connection.class);
        PreparedStatement pstmtMock = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement("UPDATE job_execution SET state = ?, last_run = ? WHERE execution_id = ?"))
                .thenReturn(pstmtMock);

        //when
        jobRepository.completeExecution(jobExecution);

        //then
        verify(dataSource).getConnection();
        verify(connectionMock).prepareStatement("UPDATE job_execution SET state = ?, last_run = ? WHERE execution_id = ?");
        verify(pstmtMock, never()).setString(anyInt(), anyString());
        verify(pstmtMock, never()).setTimestamp(anyInt(), any(Timestamp.class));
        verify(pstmtMock, never()).setString(anyInt(), anyString());
        verify(pstmtMock, never()).executeUpdate();
        verify(pstmtMock).close();
        verify(connectionMock).close();
    }

    @Test
    void shouldFailWithExceptionWhenCompletingExecution() throws SQLException {
        //given
        final String jobName = "TestJob";
        final UUID executionId = UUID.randomUUID();

        JobExecution jobExecution = new JobExecution.Builder()
                .withExecutionId(executionId)
                .withJobName(jobName)
                .withState(JobState.RUNNING)
                .build();

        Connection connectionMock = mock(Connection.class);
        PreparedStatement pstmtMock = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenThrow(new SQLException());

        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jobRepository.completeExecution(jobExecution));

        //then
        assertEquals(String.format(FAILED_TO_COMPLETE_EXECUTION_MSG, jobName, executionId), exception.getMessage());
        verify(dataSource).getConnection();
        verify(connectionMock, never()).prepareStatement(anyString());
        verify(pstmtMock, never()).executeUpdate();
    }
}
