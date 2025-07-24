package io.github.sergejsvisockis.jobs;

/**
 * Job execution exception.
 * Advised to be thrown in every batch job execution once an exception occurs.
 * This is a checked exception to have a more struct control over the error handling.
 */
public class JobExecutionException extends Exception {

    public JobExecutionException() {
    }

    public JobExecutionException(String message) {
        super(message);
    }

    public JobExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobExecutionException(Throwable cause) {
        super(cause);
    }
}
