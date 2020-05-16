package net.javaci.bank.util;

public class AccountNumberGenerator {
	
	public String generateAccountNumber(String customerCitizenNumber, int numberOfAccount) {
		return customerCitizenNumber + "-" + String.format("%05d", numberOfAccount);
	}
}
