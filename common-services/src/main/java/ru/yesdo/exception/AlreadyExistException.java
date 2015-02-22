package ru.yesdo.exception;

/**
 * Created by lameroot on 19.02.15.
 */
public class AlreadyExistException extends RuntimeException {

    private String name;

    public AlreadyExistException(String name) {
        this.name = name;
    }

    public AlreadyExistException(String message, String name) {
        super(message);
        this.name = name;
    }

    public AlreadyExistException(String message, Throwable cause, String name) {
        super(message, cause);
        this.name = name;
    }

    public AlreadyExistException(Throwable cause, String name) {
        super(cause);
        this.name = name;
    }

    public AlreadyExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String name) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.name = name;
    }

    @Override
    public String getMessage() {
        return (null != super.getMessage() ? super.getMessage() : "Object with thi name already exist in store") + ", name: " + name;
    }
}
