package net.javaci.bank.api.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.ApiParam;
import net.javaci.bank.db.dao.ExchangeRateDao;
import net.javaci.bank.db.model.ExchangeRate;

@RestController
@RequestMapping("/api/foreign-exchange")
public class ExchangeRateApi {

	@Autowired private ExchangeRateDao exchangeRateDao;
	
	@GetMapping("/findByLocalDate")
    @ResponseBody
    public List<ExchangeRate> findByLocalDate(
    		@ApiParam(value = "Date in yyyy-MM-dd iso date format. Cannot be empty.", required = true, example = "2020-12-31") 
    		@PathVariable String dateStr
    ) {
		LocalDate date = null;
		try {
			LocalDate.parse(dateStr);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date must be in yyyy-MM-dd iso date format. Given input: " + dateStr);
		}
        return exchangeRateDao.findAllByDate(date);
    }
}
