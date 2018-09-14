package pl.spray.restdemo.transit.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

import pl.spray.restdemo.transit.dao.TransitDAO;
import pl.spray.restdemo.transit.model.TransitModel;

@Service("TransitService")
public class TransitService {
	
	@Autowired
	TransitDAO dao;
	
	public TransitModel addTransit(TransitModel transit) throws TransactionSystemException{
		
		if(transit.getDate() == null) {
			transit.setDate(LocalDate.now());
		}
		return dao.save(transit);
	}	
	
	public JSONObject getDailyRaport(LocalDate startDate, LocalDate endDate){
		
		List<TransitModel> transits = dao.findAllByDateBetween(startDate, endDate);
		int price = 0;
		BigDecimal distance = new BigDecimal(0);
		JSONObject distanceJson = new JSONObject();
		
		if(!transits.isEmpty()) {
			for(TransitModel transit: transits) {
				distance = distance.add(transit.getDistance());
				price += transit.getPrice();
			}			
			distanceJson.put("total_distance", distance);
			distanceJson.put("total_price", price);
		} else {
			distanceJson.put("message","There is no transtis between those dates");
		}
		return distanceJson;
	}
}
