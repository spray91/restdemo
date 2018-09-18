package pl.spray.restdemo.transit.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Async
	public void getDistance(Long id) throws InterruptedException, JSONException {

		logger.info(String.format("Processing request with id %d", id));

		Optional<TransitModel> transitmodel = dao.findById(id);

		BigDecimal distance = utilities.getDistance(transitmodel.get().getSource(),
				transitmodel.get().getDestination());

		if (distance.compareTo(BigDecimal.ZERO) < 0) {
			transitmodel.get().setErrorMssage("WWe are not able to determine the distance between the locations");
			logger.info(String.format("Request with id %d was finished with errors", id));
		} else {
			distance = distance.multiply(new BigDecimal(1.609));
			transitmodel.get().setDistance(distance);
			logger.info(String.format("Request with id %d was finished without errors", id));
		}

		dao.save(transitmodel.get());
	}
}