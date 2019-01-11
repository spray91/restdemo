package pl.spray.restdemo.transit.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import pl.spray.restdemo.transit.dao.TransitDAO;
import pl.spray.restdemo.transit.model.Transit;

@Service("TransitService")
public class TransitService {

	private TransitDAO dao;
	private DistanceService distanceService;

	@Autowired
	public TransitService(TransitDAO dao, DistanceService distanceService){
		this.dao = dao;
		this.distanceService = distanceService;
	}

	public ResponseEntity addTransit(Transit transit) {

		if (transit.getDate() == null) {
			transit.setDate(LocalDate.now());
		}
		dao.save(transit);
		distanceService.getDistance(transit);

		return ResponseEntity.created(null).contentType(MediaType.APPLICATION_JSON).body(transit);
	}
}
