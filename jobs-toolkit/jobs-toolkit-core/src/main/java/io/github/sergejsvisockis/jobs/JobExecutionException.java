package io.github.sergejsvisockis.jobs;

/**
 * Job execution exception.
 * Advised to be thrown in every batch job execution once an exception occurs.
 * This is a checked exception to have a more struct control over the error handling.
 */
public class JobExecutionException extends Exception {

    /**
     * Default constructor.
     */
    public JobExecutionException() {
    }

    /**
     * Constructs a JobExecutionException with a specified message.
     *
     * @param message the detail message.
     */
    public JobExecutionException(String message) {
        super(message);
    }

    /**
     * Constructs a JobExecutionException with a specified message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause of the exception.
     */
    public JobExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a JobExecutionException with a specified cause.
     *
     * @param cause the cause of the exception.
     */
    public JobExecutionException(Throwable cause) {
        super(cause);
    }
}
