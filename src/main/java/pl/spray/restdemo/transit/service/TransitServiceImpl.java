package pl.spray.restdemo.transit.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

import pl.spray.restdemo.transit.dao.TransitDAO;
import pl.spray.restdemo.transit.model.TransitModel;

@Service("TransitService")
public class TransitServiceImpl implements TransitService {
	
	@Autowired
	TransitDAO dao;
	
	public TransitModel addTransit(TransitModel transit) throws TransactionSystemException{
		
		if(transit.getDate() == null) {
			transit.setDate(LocalDate.now());
		}
		return dao.save(transit);
	}	
}
