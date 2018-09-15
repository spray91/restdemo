package pl.spray.restdemo.transit.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import pl.spray.restdemo.transit.dao.TransitDAO;
import pl.spray.restdemo.transit.model.TransitModel;

@Service
public class DistanceService {

	private static final Logger logger = LoggerFactory.getLogger(DistanceService.class);

	@Autowired
	private TransitDAO dao;

	@Value("${mapquestapi.key}")
	private String key;

	@Async
	public void getDistance(Long id) throws InterruptedException, JSONException {

		logger.info(String.format("Processing request with id %d", id));
		
		BigDecimal distance;
		RestTemplate restTemplate = new RestTemplate();
		String url = String.format("http://www.mapquestapi.com/directions/v2/routematrix?key=%s", key);

		Optional<TransitModel> transitmodel = dao.findById(id);

		String requestJson = String.format("{\"locations\": [\"%s\" , \"%s\"]}", transitmodel.get().getSource(),
				transitmodel.get().getDestination());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);

		ResponseEntity<String> distanceResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		
		JSONObject distanceJson = new JSONObject(distanceResponse.getBody());
		
		try {
			distance = BigDecimal.valueOf(distanceJson.getJSONArray("distance").getDouble(distanceJson.getJSONArray("distance").length() - 1));
		} catch (JSONException ex) {

			transitmodel.get().setErrorMssage(distanceJson.getJSONObject("info").getJSONArray("messages").getString(0).toString());
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