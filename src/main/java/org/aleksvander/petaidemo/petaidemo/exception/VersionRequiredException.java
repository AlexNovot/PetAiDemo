package org.aleksvander.petaidemo.petaidemo.exception;

public class VersionRequiredException extends RuntimeException {

    public VersionRequiredException(String message) {
        super(message);
    }
}
