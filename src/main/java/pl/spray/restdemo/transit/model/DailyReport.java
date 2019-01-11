package pl.spray.restdemo.transit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class DailyReport {

    @NotBlank
    @JsonProperty("start_date")
    private LocalDate startDate;

    @NotBlank
    @JsonProperty("end_date")
    private LocalDate endDate;
}
