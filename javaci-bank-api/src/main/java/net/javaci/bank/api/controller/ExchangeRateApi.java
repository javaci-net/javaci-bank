package net.javaci.bank.api.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.javaci.bank.db.dao.ExchangeRateDao;
import net.javaci.bank.db.model.ExchangeRate;

@RestController
@RequestMapping("/api/foreign-exchange")
public class ExchangeRateApi {

	@Autowired private ExchangeRateDao exchangeRateDao;
	
	@GetMapping("/findByLocalDate")
    @ResponseBody
    public List<ExchangeRate> findByLocalDate(@RequestBody LocalDate date) {
        return exchangeRateDao.findAllByDate(date);
    }
}
