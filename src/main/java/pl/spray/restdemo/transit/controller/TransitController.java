package pl.spray.restdemo.transit.controller;

import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pl.spray.restdemo.transit.model.TransitModel;
import pl.spray.restdemo.transit.service.TransitService;

@Slf4j
@RestController
public class TransitController {

	//TODO: modify get methods to use body instead of param

	@Autowired
	TransitService transitService;

	@PostMapping(value = "/transits")
	public ResponseEntity addTransit(@RequestBody TransitModel transitmodel) throws InterruptedException {
		return transitService.addTransit(transitmodel);
	}

	@GetMapping(value = "/reports/daily", produces = "application/json")
	public ResponseEntity<?> getDailyReport(@RequestParam(value = "start_date") String startDate,
			@RequestParam(value = "end_date") String endDate) throws JSONException {

		return ResponseEntity.ok()
				.body(transitService.getDailyRaport(LocalDate.parse(startDate), LocalDate.parse(endDate)).toString());
	}

	@GetMapping(value = "/reports/currentmonth", produces = "application/json")
	public ResponseEntity<?> getCurrentMonthReport() throws JSONException {

		LocalDate firstDayOfMonth = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth() - 1);

		return ResponseEntity.ok()
				.body(transitService.getMonthlyReport(firstDayOfMonth, LocalDate.now().getDayOfMonth()).toString());
	}

	@GetMapping(value = "/reports/lastmonth", produces = "application/json")
	public ResponseEntity<?> getLastMonthReport() throws JSONException {

		LocalDate firstDayOfLastMonth = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth() - 1).minusMonths(1);
		int days = LocalDate.now().minusMonths(1).getMonth().length(LocalDate.now().isLeapYear());

		return ResponseEntity.ok().body(transitService.getMonthlyReport(firstDayOfLastMonth, days).toString());
	}
}
