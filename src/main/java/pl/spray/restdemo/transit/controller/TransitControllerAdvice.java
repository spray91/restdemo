package pl.spray.restdemo.transit.controller;

import java.time.format.DateTimeParseException;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TransitControllerAdvice {

	@ExceptionHandler(DateTimeParseException.class)
	public ResponseEntity<String> handleDateTimeParseException(DateTimeParseException ex) throws JSONException {

		JSONObject responseJson = new JSONObject();
		responseJson.put("message", ex.getMessage());
		return ResponseEntity.badRequest().header("Content-Type", "application/json").body(responseJson.toString());

	}

	@ExceptionHandler(TransactionSystemException.class)
	public ResponseEntity<String> handleTransactionSystemException(TransactionSystemException ex) throws JSONException {

		JSONObject responseJson = new JSONObject();
		responseJson.put("message", "Source, destination and cost cannot be empty");
		return ResponseEntity.badRequest().header("Content-Type", "application/json").body(responseJson.toString());

	}

}
