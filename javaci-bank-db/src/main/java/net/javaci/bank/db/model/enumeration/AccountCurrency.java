package net.javaci.bank.db.model.enumeration;

public enum AccountCurrency {
    TL("TRY"),
    USD("USD"),
    EURO("EUR");

    public final String code;

    AccountCurrency(String code) {
        this.code = code;
    }

    public static AccountCurrency valueOfCode(String code) {
        for (AccountCurrency c : values()) {
            if (c.code.equals(code)) {
                return c;
            }
        }
        return null;
    }
}
