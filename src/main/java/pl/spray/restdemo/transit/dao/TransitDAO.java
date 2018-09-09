package pl.spray.restdemo.transit.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pl.spray.restdemo.transit.model.TransitModel;


@Repository
public interface TransitDAO extends CrudRepository<TransitModel, Integer> {

}