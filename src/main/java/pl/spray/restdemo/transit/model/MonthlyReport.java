package pl.spray.restdemo.transit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.YearMonth;

@Data
public class MonthlyReport {

    @NotBlank
    @JsonProperty("year_month")
    private YearMonth yearMonth;
}
