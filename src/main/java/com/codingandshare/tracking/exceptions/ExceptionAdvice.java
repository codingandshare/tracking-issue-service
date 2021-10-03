package com.codingandshare.tracking.exceptions;

import com.codingandshare.tracking.dtos.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

/**
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@ControllerAdvice
public class ExceptionAdvice {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ResponseObject> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    ResponseObject responseObject = new ResponseObject();
    responseObject.setMessage(Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage());
    return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<ResponseObject> handleIllegalArgumentException(IllegalArgumentException ex) {
    ResponseObject responseObject = new ResponseObject();
    responseObject.setMessage(ex.getMessage());
    return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(EnumValidationException.class)
  ResponseEntity<ResponseObject> handleEnumValidationException(EnumValidationException ex) {
    ResponseObject responseObject = new ResponseObject();
    responseObject.setMessage(ex.getMessage());
    return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NotFoundException.class)
  ResponseEntity<ResponseObject> handleNotFoundException(NotFoundException ex) {
    ResponseObject responseObject = new ResponseObject();
    responseObject.setMessage(ex.getMessage());
    return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(TokenInvalidException.class)
  ResponseEntity<ResponseObject> handleTokenInvalidException(TokenInvalidException ex) {
    ResponseObject responseObject = new ResponseObject();
    responseObject.setMessage(ex.getMessage());
    return new ResponseEntity<>(responseObject, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  ResponseEntity<ResponseObject> handleConstraintViolationException(ConstraintViolationException ex) {
    ResponseObject responseObject = new ResponseObject();
    responseObject.setMessage(ex.getMessage());
    ex.getConstraintViolations().stream().findFirst().ifPresent((it) -> {
      responseObject.setMessage(it.getMessage());
    });
    return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
  }
}
