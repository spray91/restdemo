package pl.spray.restdemo.transit.controller;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControlerErrorAdvice {

	Logger LOGGER = LoggerFactory.getLogger(ControlerErrorAdvice.class);

	@ExceptionHandler(value = { TransactionSystemException.class })
	protected ResponseEntity<?> handleConflict(TransactionSystemException e) {
		LOGGER.error("Caught", e);
		Throwable cause = e.getRootCause();

		if (cause instanceof ConstraintViolationException) {
			return new ResponseEntity<>(e.getCause().getMessage(), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}
}
