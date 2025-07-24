package io.github.sergejsvisockis.jobs.repository;

import io.github.sergejsvisockis.jobs.JobDefinition;
import io.github.sergejsvisockis.jobs.JobExecution;
import io.github.sergejsvisockis.jobs.JobState;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static io.github.sergejsvisockis.jobs.JobState.FAILED;

public class JdbcJobRepository implements JobRepository {

    private static final String QUERY_FIND_JOB_DEFINITION = "SELECT j.job_name, j.job_description FROM job_definition j WHERE job_name = ?";
    private static final String QUERY_SAVE_JOB_EXECUTION = "INSERT INTO job_execution(execution_id, job_name, last_run, state) VALUES (?, ?, ?, ?)";
    private static final String QUERY_UPDATE_EXECUTION_STATUS = "UPDATE job_execution SET state = ?, last_run = ? WHERE execution_id = ?";
    private static final String QUERY_FIND_LAST_JOB_EXECUTION = "SELECT j.execution_id, j.job_name, j.last_run, j.state FROM job_execution j WHERE job_name = ?";

    static final String FAILED_TO_GET_JOB_DEFINITION_MSG = "Failed to get job definition for a job with name=%s";
    static final String FAILED_TO_CAPTURE_EXECUTION_MSG = "Failed to capture execution for a job with name=%s";
    static final String FAILED_TO_UPDATE_EXECUTION = "Failed to update execution for a job with name=%s executionId=%s";
    static final String FAILED_TO_GET_LAST_EXECUTION_MSG = "Failed to get job last run information for a job with name=%s";
    static final String FAILED_TO_FAIL_EXECUTION_MSG = "Failed to fail execution for a job with name=%s executionId=%s";
    static final String FAILED_TO_COMPLETE_EXECUTION_MSG = "Failed to complete execution for a job with name=%s executionId=%s";

    private final DataSource dataSource;

    public JdbcJobRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<JobDefinition> fetchJobDefinition(String jobName) {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(QUERY_FIND_JOB_DEFINITION);) {

            pstmt.setString(1, jobName);
            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    String name = rs.getString(1);
                    String jobDescription = rs.getString(2);
                    return Optional.of(new JobDefinition.Builder()
                            .withJobName(name)
                            .withJobDescription(jobDescription)
                            .build());
                }

            }

            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException(String.format(FAILED_TO_GET_JOB_DEFINITION_MSG, jobName), e);
        }
    }

    @Override
    public JobExecution captureExecution(String jobName) {

        Optional<JobExecution> lastExecution = findLastExecution(jobName);

        if (lastExecution.isEmpty()) {
            return saveNewExecution(jobName);
        }

        JobExecution existingExecution = lastExecution.get();
        existingExecution.setLastRun(LocalDateTime.now());
        existingExecution.setState(JobState.RUNNING);
        return updateExecution(existingExecution);
    }

    @Override
    public JobExecution saveNewExecution(String jobName) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(QUERY_SAVE_JOB_EXECUTION)) {

            UUID jobExecutionId = UUID.randomUUID();
            LocalDateTime lastRun = LocalDateTime.now();
            JobState state = JobState.RUNNING;

            pstmt.setString(1, jobExecutionId.toString());
            pstmt.setString(2, jobName);
            pstmt.setTimestamp(3, Timestamp.valueOf(lastRun));
            pstmt.setString(4, state.name());

            pstmt.executeUpdate();

            return new JobExecution.Builder()
                    .withExecutionId(jobExecutionId)
                    .withJobName(jobName)
                    .withLastRun(lastRun)
                    .withState(state)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(String.format(FAILED_TO_CAPTURE_EXECUTION_MSG, jobName), e);
        }
    }

    @Override
    public Optional<JobExecution> findLastExecution(String jobName) {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(QUERY_FIND_LAST_JOB_EXECUTION);) {

            pstmt.setString(1, jobName);
            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    String executionId = rs.getString(1);
                    String name = rs.getString(2);
                    Timestamp lastRun = rs.getTimestamp(3);
                    String state = rs.getString(4);
                    return Optional.of(new JobExecution.Builder()
                            .withExecutionId(UUID.fromString(executionId))
                            .withLastRun(lastRun.toLocalDateTime())
                            .withState(JobState.valueOf(state))
                            .withJobName(name)
                            .build());
                }

            }

            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException(String.format(FAILED_TO_GET_LAST_EXECUTION_MSG, jobName), e);
        }
    }

    @Override
    public void failExecution(JobExecution jobExecution) {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(QUERY_UPDATE_EXECUTION_STATUS)) {

            pstmt.setString(1, FAILED.name());
            pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(3, jobExecution.getExecutionId().toString());

            pstmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(String.format(FAILED_TO_FAIL_EXECUTION_MSG,
                    jobExecution.getJobName(), jobExecution.getExecutionId().toString()), e);
        }
    }

    @Override
    public void completeExecution(JobExecution jobExecution) {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(QUERY_UPDATE_EXECUTION_STATUS)) {

            if (!FAILED.equals(jobExecution.getState())) {

                pstmt.setString(1, JobState.COMPLETED.name());
                pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                pstmt.setString(3, jobExecution.getExecutionId().toString());

                pstmt.executeUpdate();
            }

        } catch (Exception e) {
            throw new RuntimeException(String.format(FAILED_TO_COMPLETE_EXECUTION_MSG,
                    jobExecution.getJobName(), jobExecution.getExecutionId().toString()), e);
        }
    }

    @Override
    public JobExecution updateExecution(JobExecution jobExecution) {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(QUERY_UPDATE_EXECUTION_STATUS)) {

            pstmt.setString(1, jobExecution.getState().name());
            pstmt.setTimestamp(2, Timestamp.valueOf(jobExecution.getLastRun()));
            pstmt.setString(3, jobExecution.getExecutionId().toString());

            pstmt.executeUpdate();

            return jobExecution;
        } catch (Exception e) {
            throw new RuntimeException(String
                    .format(FAILED_TO_UPDATE_EXECUTION,
                            jobExecution.getJobName(), jobExecution.getExecutionId().toString()), e);
        }
    }
}
