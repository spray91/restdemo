package pl.spray.restdemo.transit.controller;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	TransitModel addTransit(@RequestBody TransitModel transitmodel) throws JSONException, InterruptedException {

		logger.info("Received request");
		TransitModel response = dao.save(transitmodel);
		ds.getDistance(transitmodel.getId());
		
		return response;
	}
}
