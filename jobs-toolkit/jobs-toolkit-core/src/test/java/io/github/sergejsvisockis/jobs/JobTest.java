package io.github.sergejsvisockis.jobs;

import io.github.sergejsvisockis.jobs.lockrepository.LockRepository;
import io.github.sergejsvisockis.jobs.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static io.github.sergejsvisockis.jobs.AbstractJob.JOB_FAILED;
import static io.github.sergejsvisockis.jobs.AbstractJob.JOB_NOT_FOUND_MSG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobTest {

    @Test
    void shouldExecuteTheWholeJobFlow() {
        //given
        final String jobName = "TestJob";
        JobRepository jobRepository = mock(JobRepository.class);
        LockRepository lockRepository = mock(LockRepository.class);

        JobDefinition jobDefinition = new JobDefinition.Builder()
                .withJobName(jobName)
                .withJobDescription("Test job description")
                .build();

        LockMetadata lockMetadata = new LockMetadata.Builder()
                .withJobName(jobName)
                .withLockedAt(LocalDateTime.now())
                .withLockId(UUID.randomUUID())
                .build();

        JobExecution jobExecution = new JobExecution.Builder()
                .withJobName(jobName)
                .withExecutionId(UUID.randomUUID())
                .withLastRun(LocalDateTime.now())
                .withState(JobState.RUNNING)
                .build();

        when(jobRepository.fetchJobDefinition(eq(jobName))).thenReturn(Optional.of(jobDefinition));
        when(lockRepository.fetchLockMetadata(eq(jobName))).thenReturn(Optional.empty());
        when(lockRepository.acquireLock(eq(jobName))).thenReturn(lockMetadata);
        when(jobRepository.captureExecution(eq(jobName))).thenReturn(jobExecution);

        TestJob testJob = new TestJob(jobRepository, lockRepository);

        //when
        testJob.doExecute();

        //then
        // Verify that the job execution was captured
        verify(jobRepository).fetchJobDefinition(eq(jobName));
        verify(lockRepository).fetchLockMetadata(eq(jobName));
        verify(lockRepository).acquireLock(eq(jobName));
        verify(jobRepository).captureExecution(eq(jobName));
        verify(jobRepository).completeExecution(eq(jobExecution));
        verify(lockRepository).releaseLock(eq(lockMetadata));
    }

    @Test
    void shouldFailWithExceptionWhenNoJobDefinitionFound() {
        //given
        final String jobName = "TestJob";
        JobRepository jobRepository = mock(JobRepository.class);
        when(jobRepository.fetchJobDefinition(eq(jobName))).thenReturn(Optional.empty());
        LockRepository lockRepository = mock(LockRepository.class);

        TestJob testJob = new TestJob(jobRepository, lockRepository);

        //when
        IllegalStateException exception = assertThrows(IllegalStateException.class, testJob::doExecute);

        //then
        assertEquals(String.format(JOB_NOT_FOUND_MSG, jobName), exception.getMessage());
        verify(jobRepository).fetchJobDefinition(eq(jobName));
        verify(lockRepository, never()).fetchLockMetadata(eq(jobName));
        verify(lockRepository, never()).acquireLock(eq(jobName));
        verify(jobRepository, never()).captureExecution(eq(jobName));
        verify(jobRepository, never()).completeExecution(any(JobExecution.class));
        verify(lockRepository, never()).releaseLock(any(LockMetadata.class));
    }

    @Test
    void shouldSkipStartingANewInstanceWhenJobIsAlreadyRun() {
        //given
        final String jobName = "TestJob";
        JobRepository jobRepository = mock(JobRepository.class);
        LockRepository lockRepository = mock(LockRepository.class);

        JobDefinition jobDefinition = new JobDefinition.Builder()
                .withJobName(jobName)
                .withJobDescription("Test job description")
                .build();

        LockMetadata lockMetadata = new LockMetadata.Builder()
                .withJobName(jobName)
                .withLockedAt(LocalDateTime.now())
                .withLockId(UUID.randomUUID())
                .build();

        when(jobRepository.fetchJobDefinition(eq(jobName))).thenReturn(Optional.of(jobDefinition));
        when(lockRepository.fetchLockMetadata(eq(jobName))).thenReturn(Optional.of(lockMetadata));

        TestJob testJob = new TestJob(jobRepository, lockRepository);

        //when
        testJob.doExecute();

        //then
        verify(jobRepository).fetchJobDefinition(eq(jobName));
        verify(lockRepository).fetchLockMetadata(eq(jobName));
        verify(lockRepository, never()).acquireLock(eq(jobName));
        verify(jobRepository, never()).captureExecution(eq(jobName));
        verify(jobRepository, never()).completeExecution(any(JobExecution.class));
        verify(lockRepository, never()).releaseLock(any(LockMetadata.class));
    }

    @Test
    void shouldFailExecutionOnceTheJobFailed() {
        //given
        final String jobName = "SecondTestJob";
        JobRepository jobRepository = mock(JobRepository.class);
        LockRepository lockRepository = mock(LockRepository.class);

        JobDefinition jobDefinition = new JobDefinition.Builder()
                .withJobName(jobName)
                .withJobDescription("Test job description")
                .build();

        LockMetadata lockMetadata = new LockMetadata.Builder()
                .withJobName(jobName)
                .withLockedAt(LocalDateTime.now())
                .withLockId(UUID.randomUUID())
                .build();

        JobExecution jobExecution = new JobExecution.Builder()
                .withJobName(jobName)
                .withExecutionId(UUID.randomUUID())
                .withLastRun(LocalDateTime.now())
                .withState(JobState.RUNNING)
                .build();

        when(jobRepository.fetchJobDefinition(eq(jobName))).thenReturn(Optional.of(jobDefinition));
        when(lockRepository.fetchLockMetadata(eq(jobName))).thenReturn(Optional.empty());
        when(lockRepository.acquireLock(eq(jobName))).thenReturn(lockMetadata);
        when(jobRepository.captureExecution(eq(jobName))).thenReturn(jobExecution);

        SecondTestJob testJob = new SecondTestJob(jobRepository, lockRepository);

        //when
        IllegalStateException exception = assertThrows(IllegalStateException.class, testJob::doExecute);

        //then
        assertEquals(String.format(JOB_FAILED, jobName), exception.getMessage());
        verify(jobRepository).fetchJobDefinition(eq(jobName));
        verify(lockRepository).fetchLockMetadata(eq(jobName));
        verify(lockRepository).acquireLock(eq(jobName));
        verify(jobRepository).captureExecution(eq(jobName));
        verify(jobRepository, never()).completeExecution(eq(jobExecution));
        verify(jobRepository).failExecution(eq(jobExecution));
        verify(lockRepository).releaseLock(eq(lockMetadata));
    }

    public static class TestJob extends AbstractJob {

        public TestJob(JobRepository jobRepository, LockRepository lockRepository) {
            super(jobRepository, lockRepository);
        }

        @Override
        public void run() {
            super.run();
        }

        @Override
        public void execute() throws JobExecutionException {

        }

        @Override
        public String getJobName() {
            return "TestJob";
        }
    }

    public static class SecondTestJob extends AbstractJob {

        public SecondTestJob(JobRepository jobRepository, LockRepository lockRepository) {
            super(jobRepository, lockRepository);
        }

        @Override
        public void run() {
            super.run();
        }

        @Override
        public void execute() throws JobExecutionException {
            throw new JobExecutionException();
        }

        @Override
        public String getJobName() {
            return "SecondTestJob";
        }
    }
}
