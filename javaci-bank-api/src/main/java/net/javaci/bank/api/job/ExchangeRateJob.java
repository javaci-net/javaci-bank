package net.javaci.bank.api.job;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import net.javaci.bank.db.dao.ExchangeRateDao;
import net.javaci.bank.db.model.ExchangeRate;
import net.javaci.bank.db.model.enumaration.AccountCurrency;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ExchangeRateJob extends QuartzJobBean {

    /*
    Api key for https://www.exchangerate-api.com/docs/java-currency-api
     */
    private static final String API_KEY = "4149f55af2ac524805bc8dd6";

    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD";

    @Autowired
    private ExchangeRateDao exchangeRateDao;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());
        List<ExchangeRate> rates = new ArrayList<>();
        try {
            rates = retrieveExchangeRates();
        } catch (IOException e) {
            log.error("Exception occured while retrieving exchange rates", e);
            return;
        }
        if (rates == null) {
            return; // error varsa iceride log lanmistir
        }

        for (ExchangeRate er : rates) {
            if (exchangeRateDao.existsByDateAndCurrency(er.getDate(), er.getCurrency())) {
                continue;
            }
            exchangeRateDao.save(er);
        }

    }

    private List<ExchangeRate> retrieveExchangeRates() throws IOException {
        HttpURLConnection request = (HttpURLConnection) new URL(API_URL).openConnection();
        request.connect();
        Map root = new Gson().fromJson(new InputStreamReader((InputStream) request.getContent()), Map.class);
        if (!root.get("result").equals("success")) {
            log.error("Exchange rate retrieve is not successfull. result:" + root.get("result"));
            return null;
        }

        List<ExchangeRate> exchangeRateList = new ArrayList<>();
        Map conversionRates = (Map) root.get("conversion_rates");
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("E, dd MMM yyyy hh:mm:ss Z"); //"Sat, 28 Mar 2020 00:00:00 +0000",
        LocalDate date = LocalDate.parse((String)root.get("time_last_update_utc"), dtf);
        for (AccountCurrency currency : AccountCurrency.values()) {
            if (currency == AccountCurrency.USD)
                continue; // USD yi kaydetmeye gerek yok cunku zaten 1 USD kac TL seklinde kaydediyoruz
            Double rate = (Double)conversionRates.get(currency.code);
            if (rate == null) {
                log.error("Rate is not found in response of exchange rate API root/conversion_rates for currency:" + currency);
                continue;
            }
            ExchangeRate er = new ExchangeRate(null, date, currency, BigDecimal.valueOf(rate));
            exchangeRateList.add(er);
        }
        return exchangeRateList;
    }
}