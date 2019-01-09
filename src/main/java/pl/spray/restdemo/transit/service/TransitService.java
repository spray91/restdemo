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
import pl.spray.restdemo.transit.model.TransitModel;

@Service("TransitService")
public class TransitService {
	//TODO: this reporting sucks, refactor all
	public final static String ERRORS = "errors";

	@Autowired
	TransitDAO dao;

	@Autowired
	DistanceService distanceService;

	public ResponseEntity addTransit(TransitModel transit) throws InterruptedException {

		if (transit.getDate() == null) {
			transit.setDate(LocalDate.now());
		}
		dao.save(transit);
		distanceService.getDistance(transit);

		return ResponseEntity.created(null).contentType(MediaType.APPLICATION_JSON).body(transit);
	}

	public ResponseEntity<JSONObject> getDailyRaport(LocalDate startDate, LocalDate endDate) throws JSONException {
		List<TransitModel> transits = dao.findAllByDateBetween(startDate, endDate);
		JSONObject reportJson = new JSONObject();

		if (!transits.isEmpty()) {
			Map<String, Object> priceAndDistance = countPriceAndDistance(transits);
			reportJson.put("total_distance", priceAndDistance.get("distance"))
				.put("total_price", priceAndDistance.get("price"))
				.putOpt(ERRORS, priceAndDistance.get("errorArray"));
		} else {
			reportJson.put("message", "There is no transits between those dates.");
		}
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(reportJson);
	}

	public JSONObject getMonthlyReport(LocalDate from, int days) throws JSONException {
		JSONObject reportJson = new JSONObject();
		JSONArray jsonarray = new JSONArray();

		for (int i = 0; i < days; i++) {
			List<TransitModel> transits = dao.findAllByDate(from.plusDays(i));
			if (!transits.isEmpty()) {
				Map<String, Object> priceAndDistance = countPriceAndDistance(transits);
				BigDecimal distance = (BigDecimal) priceAndDistance.get("distance");
				BigDecimal price = (BigDecimal) priceAndDistance.get("price");
				JSONArray errorArray = (JSONArray) priceAndDistance.get("errorArray");
				jsonarray.put(
						new JSONObject().put("date", from.plusDays(i)).put("total_distance", distance.toString() + "km")
								.put("avg_distance", distance.divide(new BigDecimal(transits.size())).toString() + "km")
								.put("total_price", price.setScale(2).toString() + "PLN")
								.put("avg_price",
										price.divide(new BigDecimal(transits.size()).setScale(2)).toString() + "PLN")
								.putOpt(ERRORS, errorArray));
			} else {
				jsonarray.put(new JSONObject().put("date", from.plusDays(i)).put("message",
						"There was no transit in this day"));
			}
		}

		return reportJson.put("scope", new JSONObject().put("from:", from).put("to", from.plusDays(days - 1)))
				.put("CurrentMonthReport", jsonarray);
	}


	private JSONObject createErrorJSON(TransitModel transit) throws JSONException {

		return new JSONObject().put("Transit source", transit.getSource())
				.put("transit_destination", transit.getDestination()).put("distance", 0)
				.put("error_message", transit.getErrorMessage());
	}

	private Map<String, Object> countPriceAndDistance(List<TransitModel> transits){
		BigDecimal price = new BigDecimal(0);
		BigDecimal distance = new BigDecimal(0);
		JSONArray errorArray = new JSONArray();
		for (TransitModel transit : transits) {
			if (transit.getDistance() != null)
				distance = distance.add(transit.getDistance());
			else {
				errorArray.put(createErrorJSON(transit));
			}
			price = price.add(transit.getPrice());
		}
		Map<String, Object> output = new HashMap<>();
		output.put("distance", distance);
		output.put("price", price);
		output.put("errorArray",errorArray);
		return output;
	}
}
