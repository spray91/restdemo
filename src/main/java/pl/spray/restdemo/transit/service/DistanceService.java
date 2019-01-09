package pl.spray.restdemo.transit.service;

import java.math.BigDecimal;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;
import pl.spray.restdemo.transit.dao.TransitDAO;
import pl.spray.restdemo.transit.model.TransitModel;

@Service
@Slf4j
public class DistanceService {

    //TODO: improve getDistanceFromExternalApi (error handling, bad parameters etc? )
	//TODO: should it be splitted? (async and sync mehtod in one class)

	@Value("${mapquestapi.key}")
	private String key;

	private final static String MAPQUEST_URL = "http://www.mapquestapi.com/directions/v2/routematrix?key=%s";

	@Autowired
	private TransitDAO dao;

	@Async
	public void getDistance(TransitModel transitModel) throws JSONException {

		log.info(String.format("Processing request with id %d", transitModel.getId()));

		BigDecimal distance = getDistanceFromExternalApi(transitModel.getSource(), transitModel.getDestination());

		if (distance.compareTo(BigDecimal.ZERO) < 0) {
			transitModel.setErrorMessage("We are not able to determine the distance between the locations.");
			log.info(String.format("Request with id %d was finished with errors", transitModel.getId()));
		} else {
			distance = distance.multiply(new BigDecimal(1.609));
			transitModel.setDistance(distance);
			log.info(String.format("Request with id %d was finished without errors", transitModel.getId()));
		}
		dao.save(transitModel);
	}

	private BigDecimal getDistanceFromExternalApi(String source, String destination) throws JSONException {

		RestTemplate restTemplate = new RestTemplate();

		String url = String.format(MAPQUEST_URL, key);
		HttpEntity<String> entity = prepareHttpEntity(source, destination);

		ResponseEntity<String> distanceResponse = restTemplate.postForEntity(url, entity, String.class);

		return getDistanceFromJson(distanceResponse.getBody());
	}

	private HttpEntity<String> prepareHttpEntity(String source, String destination){

		String payload = String.format("{\"locations\": [\"%s\" , \"%s\"]}", source, destination);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new HttpEntity<>(payload, headers);
	}

	private BigDecimal getDistanceFromJson(String payload){
		JSONObject distanceJson = new JSONObject(payload);
		try {
			return BigDecimal.valueOf(distanceJson.getJSONArray("distance")
					.getDouble(distanceJson.getJSONArray("distance").length() - 1));
		} catch (JSONException ex) {
			return new BigDecimal(-1);
		}
	}
}