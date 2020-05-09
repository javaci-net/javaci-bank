package net.javaci.bank.db.converter;

import java.util.Currency;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.springframework.util.StringUtils;

@Converter(autoApply = true)
public class CurrencyConverter implements AttributeConverter<Currency, String> {

    @Override
    public String convertToDatabaseColumn(Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException("Use valid currency");
        }
        return currency.getCurrencyCode();
    }

    @Override
    public Currency convertToEntityAttribute(String currencyCode) {
        if (StringUtils.isEmpty(currencyCode)) {
            return null;
        }
        return Currency.getInstance(currencyCode);
    }
}
