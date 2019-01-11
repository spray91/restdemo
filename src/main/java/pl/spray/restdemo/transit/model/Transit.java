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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Entity
@Data
@Table(name = "transit")
@NoArgsConstructor
public class Transit {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank
	private String source;

	@NotBlank
	private String destination;

	@Column(scale = 2)
	@Min(value = 0)
	private BigDecimal price;

	@Column(scale = 2)
	private BigDecimal distance;

	private LocalDate date;

	private String errorMessage;
}
