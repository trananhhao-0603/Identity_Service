package vn.demo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(value = RuntimeException.class)
	ResponseEntity<String> handlingRuntimeException(RuntimeException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	// message when check validate failed
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	ResponseEntity<String> handlingNotValidException(MethodArgumentNotValidException exception) {
		return ResponseEntity.badRequest().body(exception.getFieldError().getDefaultMessage());
	}
}
