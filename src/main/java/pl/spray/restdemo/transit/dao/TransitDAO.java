package pl.spray.restdemo.transit.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.spray.restdemo.transit.model.Transit;

@Repository
public interface TransitDAO extends JpaRepository<Transit, Long> {

	List<Transit> findAllByDateBetween(LocalDate start, LocalDate end);

	List<Transit> findAllByDate(LocalDate date);
}