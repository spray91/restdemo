package pl.spray.restdemo.transit.controller;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pl.spray.restdemo.transit.dao.TransitDAO;
import pl.spray.restdemo.transit.model.TransitModel;
import pl.spray.restdemo.transit.service.DistanceService;

@RestController
public class TransitController {

	private static final Logger logger = LoggerFactory.getLogger(TransitController.class);

	@Autowired
	private TransitDAO dao;

	@Autowired
	DistanceService ds;

	@PostMapping(value = "/transits")
	public ResponseEntity<?> addTransit(@RequestBody TransitModel transitmodel)
			throws JSONException, InterruptedException, TransactionSystemException {

		logger.info("Received request");
		TransitModel response;
		try {
			logger.info("try");
			response = dao.save(transitmodel);
		} catch (TransactionSystemException e) {
			return new ResponseEntity<>(e.getCause().getMessage(), HttpStatus.BAD_REQUEST);
		}
		ds.getDistance(transitmodel.getId());

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}
