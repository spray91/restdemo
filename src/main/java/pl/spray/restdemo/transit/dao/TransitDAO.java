package pl.spray.restdemo.transit.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.spray.restdemo.transit.model.TransitModel;


@Repository
public interface TransitDAO extends JpaRepository<TransitModel, Long> {

	Optional<TransitModel> findById(Long id);
	
}