package pl.spray.restdemo.transit.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import pl.spray.restdemo.transit.dao.TransitDAO;
import pl.spray.restdemo.transit.model.TransitModel;
import pl.spray.restdemo.transit.utilities.TransitUtilities;

@Service
public class DistanceService {

	private static final Logger logger = LoggerFactory.getLogger(DistanceService.class);

	@Autowired
	private TransitDAO dao;

	@Autowired
	TransitUtilities utilities;

	@Value("${mapquestapi.key}")
	private String key;

	@Async
	public void getDistance(Long id) throws InterruptedException, JSONException {

		logger.info(String.format("Processing request with id %d", id));

		BigDecimal distance;
		Optional<TransitModel> transitmodel = dao.findById(id);

		ResponseEntity<String> distanceResponse = utilities.sendDistanceRequest(transitmodel.get().getSource(),
				transitmodel.get().getDestination());

		JSONObject distanceJson = new JSONObject(distanceResponse.getBody());

		try {
			distance = BigDecimal.valueOf(distanceJson.getJSONArray("distance")
					.getDouble(distanceJson.getJSONArray("distance").length() - 1));
		} catch (JSONException ex) {

			transitmodel.get().setErrorMssage(
					distanceJson.getJSONObject("info").getJSONArray("messages").getString(0).toString());
			dao.save(transitmodel.get());

			logger.info(String.format("Request with id %d was finished with errors", id));
			return;
		}

		distance = distance.multiply(new BigDecimal(1.609));
		transitmodel.get().setDistance(distance);

		logger.info(String.format("Request with id %d was finished without errors", id));

		dao.save(transitmodel.get());
	}
}