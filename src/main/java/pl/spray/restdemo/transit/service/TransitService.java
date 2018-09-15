package pl.spray.restdemo.transit.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

import pl.spray.restdemo.transit.controller.TransitController;
import pl.spray.restdemo.transit.dao.TransitDAO;
import pl.spray.restdemo.transit.model.TransitModel;

@Service("TransitService")
public class TransitService {

	private static final Logger logger = LoggerFactory.getLogger(TransitController.class);

	@Autowired
	TransitDAO dao;

	public TransitModel addTransit(TransitModel transit) throws TransactionSystemException {

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

		if (!transits.isEmpty()) {
			for (TransitModel transit : transits) {
				if(transit.getDistance() != null)
					distance = distance.add(transit.getDistance());
				else {
					errorArray.put(new JSONObject()
							.put("Transit source", transit.getSource())
							.put("transit_destination", transit.getDestination())
							.put("distance", 0)
							.put("error_message", transit.getErrorMssage()));
				}
				price = price.add(transit.getPrice());
			}
			reportJson.put("total_distance", distance);
			reportJson.put("total_price", price);
			if(!errorArray.isNull(0))
				reportJson.put("errors", errorArray);
		} else {
			reportJson.put("message", "There is no transtis between those dates");
		}
		return reportJson;
	}

	public JSONObject getCurrentMonthReport() throws JSONException {

		JSONObject reportJson = new JSONObject();
		int currentDay = LocalDate.now().getDayOfMonth();
		LocalDate firstDayOfMonth = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth() - 1);
		JSONArray jsonarray = new JSONArray();

		for (int i = 0; i < currentDay; i++) {
			BigDecimal price = new BigDecimal(0);
			BigDecimal distance = new BigDecimal(0);
			List<TransitModel> transits = dao.findAllByDate(firstDayOfMonth.plusDays(i));
			if (transits.size() > 0) {
				for (TransitModel transit : transits) {
					distance = distance.add(transit.getDistance());
					price = price.add(transit.getPrice());
				}
				jsonarray.put(new JSONObject()
						.put("date", firstDayOfMonth.plusDays(i))
						.put("total_distance", distance.toString() + "km")
						.put("avg_distance", distance.divide(new BigDecimal(transits.size())).toString() + "km")
						.put("total_price", price.setScale(2).toString() + "PLN")
						.put("avg_price",price.divide(new BigDecimal(transits.size()).setScale(2)).toString() + "PLN"));
			} else {
				jsonarray.put(new JSONObject().put("date", firstDayOfMonth.plusDays(i)).put("message",
						"There was no transit in this day"));
			}
		}

		return reportJson.put("CurrentMonthReport", jsonarray);
	}

	public JSONObject getLastMonthReport() throws JSONException {

		JSONObject reportJson = new JSONObject();
		LocalDate firstDayOfLastMonth = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth() - 1).minusMonths(1);
		int lastMonthMaxDays = LocalDate.now().minusMonths(1).getMonth().length(LocalDate.now().isLeapYear());

		logger.info(firstDayOfLastMonth.toString());
		logger.info(Integer.toString(lastMonthMaxDays));
		JSONArray jsonarray = new JSONArray();

		for (int i = 0; i < lastMonthMaxDays; i++) {
			BigDecimal price = new BigDecimal(0);
			BigDecimal distance = new BigDecimal(0);
			List<TransitModel> transits = dao.findAllByDate(firstDayOfLastMonth.plusDays(i));
			if (transits.size() > 0) {
				for (TransitModel transit : transits) {
					distance = distance.add(transit.getDistance());
					price = price.add(transit.getPrice());
				}
				jsonarray.put(new JSONObject().put("date", firstDayOfLastMonth.plusDays(i))
						.put("total_distance", distance.toString() + "km")
						.put("avg_distance", distance.divide(new BigDecimal(transits.size())).toString() + "km")
						.put("total_price", price.setScale(2).toString() + "PLN").put("avg_price",
								price.divide(new BigDecimal(transits.size()).setScale(2)).toString() + "PLN"));
			} else {
				jsonarray.put(new JSONObject().put("date", firstDayOfLastMonth.plusDays(i)).put("message",
						"There was no transit in this day"));
			}
		}

		return reportJson.put("LastMonthReport", jsonarray);
	}
}
