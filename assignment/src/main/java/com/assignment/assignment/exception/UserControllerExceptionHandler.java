package com.assignment.assignment.exception;

import com.assignment.assignment.controller.UserController;
import com.assignment.assignment.dto.UserResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackageClasses = UserController.class)
public class UserControllerExceptionHandler {

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<UserResponseDto> handleInvalidRequestException(InvalidRequestException ex) {
        UserResponseDto userResponseDto = failedResponse(ex.getMessage());
        return new ResponseEntity<>(userResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IdNotFoundException.class)
    public ResponseEntity<UserResponseDto> handleIdNotFoundException(IdNotFoundException ex) {
        UserResponseDto userResponseDto = failedResponse(ex.getMessage());
        return new ResponseEntity<>(userResponseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<UserResponseDto> handleUserNotFoundException(UserNotFoundException ex) {
        UserResponseDto userResponseDto = failedResponse(ex.getMessage());
        return new ResponseEntity<>(userResponseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<UserResponseDto> handleRuntimeException(RuntimeException ex) {
        UserResponseDto userResponseDto = failedResponse("System encountered error.");
        return new ResponseEntity<>(userResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UserResponseDto> handleException(Exception ex) {
        UserResponseDto userResponseDto = failedResponse("System encountered error.");
        return new ResponseEntity<>(userResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private UserResponseDto failedResponse(String errorMessage) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setErrorDescription(errorMessage);
        userResponseDto.setErrorCode("1");
        return userResponseDto;
    }
}
