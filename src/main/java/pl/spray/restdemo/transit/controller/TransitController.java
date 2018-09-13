package pl.spray.restdemo.transit.controller;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pl.spray.restdemo.transit.model.TransitModel;
import pl.spray.restdemo.transit.service.DistanceService;
import pl.spray.restdemo.transit.service.TransitService;

@RestController
public class TransitController {

	private static final Logger logger = LoggerFactory.getLogger(TransitController.class);

	@Autowired
	TransitService ts;

	@Autowired
	DistanceService ds;

	@PostMapping(value = "/transits")
	public ResponseEntity<?> addTransit(@RequestBody TransitModel transitmodel)
			throws JSONException, InterruptedException {

		logger.info("Received request");
		TransitModel response;
		try {
			response = ts.addTransit(transitmodel);

		} catch (TransactionSystemException e) {
			logger.warn("Validation Exception - bad request");
			return ResponseEntity.badRequest().body("Source, destination and cost cannot be empty");
		}
		ds.getDistance(transitmodel.getId());
		return ResponseEntity.created(null).body(response);
	}
}
