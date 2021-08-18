package ma.ynmo.cdn.exception;

public enum ErrorCodes {
    USER_NOT_FOUND(100),
    USER_NOT_VALID(101),
    PLATFORM_NOT_FOUND(200),
    PLATFORM_NOT_VALID(201) ;
    private int code;

    ErrorCodes(int code){
        this.code=code;
    }
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }



}