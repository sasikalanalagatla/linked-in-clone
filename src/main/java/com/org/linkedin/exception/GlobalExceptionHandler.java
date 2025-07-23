package com.org.linkedin.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ModelAndView handleCustomException(CustomException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                (String) exception.getErrorCode(),
                exception.getMessage(),
                LocalDateTime.now().toString()
        );
        ModelAndView mav = new ModelAndView();
        mav.addObject("error", errorResponse);
        mav.setViewName("error");
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneralException(Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                "GENERAL_ERROR",
                exception.getMessage() != null ? exception.getMessage() : "An unexpected error occurred",
                LocalDateTime.now().toString()
        );
        ModelAndView mav = new ModelAndView();
        mav.addObject("error", errorResponse);
        mav.setViewName("error");
        return mav;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException(AccessDeniedException accessDeniedException) {
        ErrorResponse errorResponse = new ErrorResponse(
                "ACCESS_DENIED",
                "You do not have permission to access this resource",
                LocalDateTime.now().toString()
        );
        ModelAndView mav = new ModelAndView();
        mav.addObject("error", errorResponse);
        mav.setViewName("error");
        return mav;
    }
}