package com.akash.OrderService.exception;

import com.akash.OrderService.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

//It means whenever there is an exception on my controller then hanldle it
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleProductServiceException(CustomException productServiceCustomException)
    {
        return new ResponseEntity<>(new ErrorResponse().builder()
                .errorMessage(productServiceCustomException.getMessage())
                .erroCode(productServiceCustomException.getErrorCode()).build(),
                HttpStatus.NOT_FOUND);
    }
}
