package vn.demo.exception;

import java.util.Map;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolation;
import vn.demo.dto.request.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String MIN_ATTRIBUTE = "min";

    @SuppressWarnings("rawtypes")
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(Exception exception) {
	ApiResponse apiResponse = new ApiResponse();

	apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
	apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

	return ResponseEntity.badRequest().body(apiResponse);
    }

    // message when check validate failed
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingNotValidException(
	    MethodArgumentNotValidException exception) {
	String enumKey = exception.getFieldError().getDefaultMessage();
	ErrorCode errorCode = ErrorCode.INVALID_KEY;
	Map<String, Object> attributes = null;

	try {
	    errorCode = ErrorCode.valueOf(enumKey);
	    var constraintViolation = exception.getBindingResult()
		    .getAllErrors().getFirst()
		    .unwrap(ConstraintViolation.class);

	    attributes = constraintViolation.getConstraintDescriptor()
		    .getAttributes();
	} catch (IllegalArgumentException e) {

	}
	ApiResponse apiResponse = new ApiResponse();

	apiResponse.setCode(errorCode.getCode());
	apiResponse.setMessage(Objects.nonNull(attributes)
		? mapAttribute(errorCode.getMessage(), attributes)
		: errorCode.getMessage());

	return ResponseEntity.badRequest().body(apiResponse);
    }

    @SuppressWarnings("rawtypes")
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
	ErrorCode errorCode = exception.getErrorCode();
	ApiResponse apiResponse = new ApiResponse();

	apiResponse.setCode(errorCode.getCode());
	apiResponse.setMessage(errorCode.getMessage());

	return ResponseEntity.status(errorCode.getStatusCode())
		.body(apiResponse);

    }

    @SuppressWarnings("rawtypes")
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(
	    AccessDeniedException exception) {
	ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
	ApiResponse apiResponse = new ApiResponse();
	apiResponse.setCode(errorCode.getCode());
	apiResponse.setMessage(errorCode.getMessage());
	return ResponseEntity.status(errorCode.getStatusCode())
		.body(apiResponse);
    }

    private String mapAttribute(String message,
	    Map<String, Object> attributes) {
	String minValue = attributes.get(MIN_ATTRIBUTE).toString();

	return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }

}
