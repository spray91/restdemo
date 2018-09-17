package pl.spray.restdemo.transit.controller;

import java.time.LocalDate;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pl.spray.restdemo.transit.dao.TransitDAO;
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

	@Autowired
	TransitDAO dao;

	@PostMapping(value = "/transits")
	public ResponseEntity<?> addTransit(@RequestBody TransitModel transitmodel)
			throws JSONException, InterruptedException {

		logger.info("Received request");

		TransitModel response = ts.addTransit(transitmodel);
		ds.getDistance(transitmodel.getId());

		return ResponseEntity.created(null).header("Content-Type", "application/json").body(response);
	}

	@GetMapping(value = "/reports/daily", produces = "application/json")
	public ResponseEntity<?> getDailyReport(@RequestParam(value = "start_date", required = true) String startDate,
			@RequestParam("end_date") String endDate) throws JSONException {

		return ResponseEntity.ok()
				.body(ts.getDailyRaport(LocalDate.parse(startDate), LocalDate.parse(endDate)).toString());
	}

	@GetMapping(value = "/reports/currentmonth", produces = "application/json")
	public ResponseEntity<?> getCurrentMonthReport() throws JSONException {

		LocalDate firstDayOfMonth = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth() - 1);

		return ResponseEntity.ok()
				.body(ts.getMonthlyReport(firstDayOfMonth, LocalDate.now().getDayOfMonth()).toString());
	}

	@GetMapping(value = "/reports/lastmonth", produces = "application/json")
	public ResponseEntity<?> getLastMonthReport() throws JSONException {

		LocalDate firstDayOfLastMonth = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth() - 1).minusMonths(1);
		int days = LocalDate.now().minusMonths(1).getMonth().length(LocalDate.now().isLeapYear());

		return ResponseEntity.ok().body(ts.getMonthlyReport(firstDayOfLastMonth, days).toString());
	}
}
