package pl.spray.restdemo.transit.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.spray.restdemo.transit.dao.TransitDAO;
import pl.spray.restdemo.transit.model.TransitModel;
import pl.spray.restdemo.transit.utilities.TransitUtilities;

@Service("TransitService")
public class TransitService {

	@Autowired
	TransitDAO dao;

	@Autowired
	TransitUtilities utilities;

	public TransitModel addTransit(TransitModel transit) {

		if (transit.getDate() == null) {
			transit.setDate(LocalDate.now());
		}
		return dao.save(transit);
	}

	public JSONObject getDailyRaport(LocalDate startDate, LocalDate endDate) throws JSONException {

		List<TransitModel> transits = dao.findAllByDateBetween(startDate, endDate);
		BigDecimal price = new BigDecimal(0);
		BigDecimal distance = new BigDecimal(0);
		JSONObject reportJson = new JSONObject();
		JSONArray errorArray = new JSONArray();
		String errorString = null;

		if (!transits.isEmpty()) {
			for (TransitModel transit : transits) {
				if (transit.getDistance() != null)
					distance = distance.add(transit.getDistance());
				else {
					errorArray.put(utilities.createErrorJSON(transit));
					errorString = "errors";
				}
				price = price.add(transit.getPrice());
			}
			reportJson.put("total_distance", distance)
				.put("total_price", price)
				.putOpt(errorString, errorArray);

		} else {
			reportJson.put("message", "There is no transtis between those dates");
		}
		return reportJson;
	}

	public JSONObject getMonthlyReport(LocalDate from, int days) throws JSONException {

		JSONObject reportJson = new JSONObject();
		JSONArray jsonarray = new JSONArray();
		JSONArray errorArray = new JSONArray();
		String errorString = null;

		for (int i = 0; i < days; i++) {
			BigDecimal price = new BigDecimal(0);
			BigDecimal distance = new BigDecimal(0);
			List<TransitModel> transits = dao.findAllByDate(from.plusDays(i));
			if (!transits.isEmpty()) {
				for (TransitModel transit : transits) {
					if (transit.getDistance() != null) {
						distance = distance.add(transit.getDistance());
					} else {
						errorArray.put(utilities.createErrorJSON(transit));
						errorString = "errors";
					}

					price = price.add(transit.getPrice());
				}
				jsonarray.put(
						new JSONObject().put("date", from.plusDays(i)).put("total_distance", distance.toString() + "km")
								.put("avg_distance", distance.divide(new BigDecimal(transits.size())).toString() + "km")
								.put("total_price", price.setScale(2).toString() + "PLN")
								.put("avg_price",
										price.divide(new BigDecimal(transits.size()).setScale(2)).toString() + "PLN")
								.putOpt(errorString, errorArray));
			} else {
				jsonarray.put(new JSONObject().put("date", from.plusDays(i)).put("message",
						"There was no transit in this day"));
			}
		}

		return reportJson.put("scope", new JSONObject().put("from:", from).put("to", from.plusDays(days - 1)))
				.put("CurrentMonthReport", jsonarray);
	}
}
