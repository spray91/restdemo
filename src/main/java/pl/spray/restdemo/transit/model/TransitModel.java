package pl.spray.restdemo.transit.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "transit")
public class TransitModel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank
	private String source;

	@NotBlank
	private String destination;

	@Column(scale = 2)
	private BigDecimal price;

	@Column(scale = 2)
	private BigDecimal distance;

	private LocalDate date;
	
	private String errorMssage;

	protected TransitModel() {
	}

	public TransitModel(String source, String destination, BigDecimal price, LocalDate date) {
		this.source = source;
		this.destination = destination;
		this.price = price;
		this.date = date;
	}

	@Override
	public String toString() {
		return String.format("ID: %d", id);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getDistance() {
		return distance;
	}

	public void setDistance(BigDecimal distance) {
		this.distance = distance;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getErrorMssage() {
		return errorMssage;
	}

	public void setErrorMssage(String errorMssage) {
		this.errorMssage = errorMssage;
	}
}
