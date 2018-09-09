package pl.spray.restdemo.transit.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import pl.spray.restdemo.transit.model.TransitModel;

@RestController
public class TransitController {

	@PostMapping(value = "/transits")
	@ResponseBody
	public void addTransit(@RequestBody TransitModel transitmodel) {

	}

}
