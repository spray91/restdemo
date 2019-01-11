package pl.spray.restdemo.transit.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.spray.restdemo.transit.dao.TransitDAO;
import pl.spray.restdemo.transit.model.DailyReport;
import pl.spray.restdemo.transit.model.MonthlyReport;
import pl.spray.restdemo.transit.model.Transit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ReportingService {

    private final static String ERRORS = "errors";
    private TransitDAO dao;

    @Autowired
    public ReportingService(TransitDAO dao){
        this.dao = dao;
    }

    public ResponseEntity getDailyReport(DailyReport reportRequest) throws JSONException {
        List<Transit> transits = dao.findAllByDateBetween(reportRequest.getStartDate(), reportRequest.getEndDate());

        JSONObject reportJson = new JSONObject();

        if (!transits.isEmpty()) {
            Map<String, Object> priceAndDistance = countPriceAndDistance(transits);
            reportJson.put("total_distance", priceAndDistance.get("distance"))
                    .put("total_price", priceAndDistance.get("price"))
                    .putOpt(ERRORS, priceAndDistance.get("errorArray"));
        } else {
            reportJson.put("message", "There is no transits between those dates.");
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(reportJson.toString());
    }

    public ResponseEntity getMonthlyReport(MonthlyReport reportRequest) throws JSONException {

        LocalDate startDate = reportRequest.getYearMonth().atDay(1);
        LocalDate endDate = reportRequest.getYearMonth().atEndOfMonth();
        if(endDate.isAfter(LocalDate.now())){
            endDate = LocalDate.now();
        }
        JSONArray reportDataArray = new JSONArray();

        for(LocalDate date = startDate ; date.isBefore(endDate.plusDays(1)) ; date = date.plusDays(1)) {
            List<Transit> transits = dao.findAllByDate(date);
            if (!transits.isEmpty()) {
                Map<String, Object> priceAndDistance = countPriceAndDistance(transits);
                BigDecimal distance = (BigDecimal) priceAndDistance.get("distance");
                BigDecimal price = (BigDecimal) priceAndDistance.get("price");
                JSONArray errorArray = (JSONArray) priceAndDistance.get("errorArray");
                reportDataArray.put(prepareReportDataArrayRecord(date, distance, price, transits.size(), errorArray));
            } else {
                reportDataArray.put(new JSONObject()
                        .put("date", date)
                        .put("message", "There was no transit in this day"));
            }
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(prepareReportJson(startDate, endDate, reportDataArray));
    }


    private JSONObject createErrorJSON(Transit transit) throws JSONException {

        return new JSONObject().put("Transit source", transit.getSource())
                .put("transit_destination", transit.getDestination()).put("distance", 0)
                .put("error_message", transit.getErrorMessage());
    }

    private Map<String, Object> countPriceAndDistance(List<Transit> transits){
        BigDecimal price = new BigDecimal(0);
        BigDecimal distance = new BigDecimal(0);
        JSONArray errorArray = new JSONArray();
        for (Transit transit : transits) {
            if (transit.getDistance() != null)
                distance = distance.add(transit.getDistance());
            else {
                errorArray.put(createErrorJSON(transit));
            }
            price = price.add(transit.getPrice());
        }
        Map<String, Object> output = new HashMap<>();
        output.put("distance", distance);
        output.put("price", price);
        output.put("errorArray",errorArray);
        return output;
    }

    private JSONObject prepareReportDataArrayRecord(LocalDate date, BigDecimal distance,
                                                    BigDecimal price, Integer transitsCount, JSONArray errorArray){
        return new JSONObject().put("date", date)
                .put("total_distance", distance.toString() + "km")
                .put("avg_distance", distance.divide(new BigDecimal(transitsCount), RoundingMode.HALF_UP).toString() + "km")
                .put("total_price", price.setScale(2, RoundingMode.HALF_UP).toString() + "PLN")
                .put("avg_price", price.divide(new BigDecimal(transitsCount).setScale(2, RoundingMode.HALF_UP), RoundingMode.HALF_UP).toString() + "PLN")
                .putOpt(ERRORS, errorArray);
    }

    private String prepareReportJson(LocalDate startDate, LocalDate endDate, JSONArray content){
        return new JSONObject().put("scope", new JSONObject().put("from:", startDate).put("to", endDate))
                .put("CurrentMonthReport", content).toString();
    }
}
