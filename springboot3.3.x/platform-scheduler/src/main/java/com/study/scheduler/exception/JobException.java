package com.study.scheduler.exception;

public class JobException extends RuntimeException {
    public JobException(String message) {
        super(message);
    }

    public JobException(String message, Throwable cause) {
        super(message, cause);
    }
}