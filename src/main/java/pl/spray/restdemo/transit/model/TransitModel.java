package pl.spray.restdemo.transit.model;

import lombok.Data;
import lombok.NoArgsConstructor;

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
@Data
@Table(name = "transit")
@NoArgsConstructor
public class TransitModel {

	//TODO: consider creation of new model just to get src and dst from REST

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

	private String errorMessage;

	@Override
	public String toString() {
		return String.format("ID: %d", id);
	}
}
