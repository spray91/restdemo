package pl.spray.restdemo.transit.controller;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.spray.restdemo.transit.model.DailyReport;
import pl.spray.restdemo.transit.model.MonthlyReport;
import pl.spray.restdemo.transit.model.Transit;
import pl.spray.restdemo.transit.service.ReportingService;
import pl.spray.restdemo.transit.service.TransitService;

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class TransitController {

	private TransitService transitService;
	private ReportingService reportingService;

	@Autowired
	public TransitController(TransitService transitService, ReportingService reportingService){
		this.transitService = transitService;
		this.reportingService = reportingService;
	}

	@PostMapping(value = "/add_transit")
	public ResponseEntity addTransit(@RequestBody Transit transit) {
		log.info(transit.toString());
		return transitService.addTransit(transit);
	}

	@GetMapping(value = "/reports/get_daily_details", produces = "application/json")
	public ResponseEntity getDailyReport(@RequestBody DailyReport reportRequest) {

		return reportingService.getDailyReport(reportRequest);
	}

	@GetMapping(value = "/reports/get_monthly", produces = "application/json")
	public ResponseEntity getMonthlyReport(@RequestBody MonthlyReport reportRequest) throws JSONException {

		return reportingService.getMonthlyReport(reportRequest);
	}
}
