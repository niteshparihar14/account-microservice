package com.elite.account.utils;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.elite.account.model.ErrorHandler;

@RestControllerAdvice
public class ControllerAdvice {
	@ExceptionHandler(value = Exception.class)
	public ErrorHandler globalExceptionHandler(Exception ex, WebRequest request) {
		return new ErrorHandler(ex.getMessage(), request.getDescription(false), LocalDateTime.now());
	}
}
