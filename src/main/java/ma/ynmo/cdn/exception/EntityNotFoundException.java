package ma.ynmo.cdn.exception;

import lombok.Getter;

public class EntityNotFoundException extends RuntimeException{
    @Getter
    private ErrorCodes errorCode;
    public EntityNotFoundException(String message) {
        super(message);
    }
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public EntityNotFoundException(String message, Throwable cause, ErrorCodes errorcode) {
        super(message, cause);
        this.errorCode=errorcode;
    }
    public EntityNotFoundException(String message, ErrorCodes errorcode) {
        super(message);
        this.errorCode=errorcode;
    }

}