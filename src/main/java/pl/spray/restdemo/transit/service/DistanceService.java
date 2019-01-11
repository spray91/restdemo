package pl.spray.restdemo.transit.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import pl.spray.restdemo.transit.dao.TransitDAO;
import pl.spray.restdemo.transit.model.Transit;

@Service
@Slf4j
public class DistanceService {

	@Value("${mapquestapi.key}")
	private String key;

	private final static String MAPQUEST_URL = "http://www.mapquestapi.com/directions/v2/routematrix?key=%s";

	private TransitDAO dao;

	@Autowired
	public DistanceService(TransitDAO dao){
		this.dao = dao;
	}

	@Async
	public void getDistance(Transit transitModel) throws JSONException {

		log.info(String.format("Processing request with id %d", transitModel.getId()));

		HashMap<String, Map> response = callExternalApi(transitModel.getSource(), transitModel.getDestination());

		if(checkIfStatusCodeFromResponseIsZero(response)){
			BigDecimal distance = getDistanceFromResponse(response);
			if(distance.compareTo(BigDecimal.ZERO) < 0){
				transitModel.setErrorMessage("We are unable to determine the distance.");
				log.info(String.format("Request with id %d was finished with errors", transitModel.getId()));
			} else {
				distance = distance.multiply(new BigDecimal(1.609));
				transitModel.setDistance(distance);
				log.info(String.format("Request with id %d was finished without errors", transitModel.getId()));
			}
		} else{
			transitModel.setErrorMessage("We are unable to determine the distance.");
			log.info(String.format("Request with id %d was finished with errors", transitModel.getId()));
		}
		dao.save(transitModel);
	}

	private HashMap<String, Map> callExternalApi(String source, String destination) throws JSONException {

		RestTemplate restTemplate = new RestTemplate();

		String url = String.format(MAPQUEST_URL, key);
		HttpEntity<String> entity = prepareHttpEntity(source, destination);
		HashMap<String, Map> response;

		try {
			response = restTemplate.postForObject(url, entity, HashMap.class);
		} catch (ResourceAccessException ex){
			log.error(ex.getMessage());
			return null;
		}
		return response;
	}

	private HttpEntity<String> prepareHttpEntity(String source, String destination){

		String payload = String.format("{\"locations\": [\"%s\" , \"%s\"]}", source, destination);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new HttpEntity<>(payload, headers);
	}

	private boolean checkIfStatusCodeFromResponseIsZero(HashMap<String, Map> response){
		if(response != null) {
			Integer statusCode = (Integer) response.get("info").get("statuscode");
			if (statusCode == 0) {
				return true;
			}
		}
		log.error("Response from external server is not OK");
		return false;
	}

	private BigDecimal getDistanceFromResponse(HashMap<String, Map> response){
		JSONArray distanceArray = new JSONArray(String.valueOf(response.get("distance")));
		try {
			return BigDecimal.valueOf(distanceArray.getDouble(distanceArray.length() - 1));
		} catch (JSONException ex) {
			return new BigDecimal(-1);
		}
	}
}