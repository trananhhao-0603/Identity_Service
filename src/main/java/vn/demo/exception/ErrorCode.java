package vn.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error",
	    HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    INVALID_KEY(1001, "Invalid key", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters",
	    HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must be at least {min} characters",
	    HttpStatus.BAD_REQUEST),
    NOT_AUTHENTICATED(1006, "User not authenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "User's age must be at least {min}",
	    HttpStatus.BAD_REQUEST),
    TOEKN_EXPIRED(1009, "Token expired", HttpStatus.UNAUTHORIZED),
    USER_NOTEXISTED(1005, "User not existed", HttpStatus.NOT_FOUND);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
	this.code = code;
	this.message = message;
	this.statusCode = statusCode;
    }

    private int code;
    private HttpStatusCode statusCode;
    private String message;

}
