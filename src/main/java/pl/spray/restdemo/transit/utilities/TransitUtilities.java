package pl.spray.restdemo.transit.utilities;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import pl.spray.restdemo.transit.model.TransitModel;

@Component
public class TransitUtilities {

	@Value("${mapquestapi.key}")
	private String key;

	public ResponseEntity<String> sendDistanceRequest(String source, String destination) {

		RestTemplate restTemplate = new RestTemplate();

		String url = String.format("http://www.mapquestapi.com/directions/v2/routematrix?key=%s", key);

		String requestJson = String.format("{\"locations\": [\"%s\" , \"%s\"]}", source, destination);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);

		return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
	}

	public JSONObject createErrorJSON(TransitModel transit) throws JSONException {

		return new JSONObject().put("Transit source", transit.getSource())
				.put("transit_destination", transit.getDestination())
				.put("distance", 0)
				.put("error_message", transit.getErrorMssage());
	}
}
