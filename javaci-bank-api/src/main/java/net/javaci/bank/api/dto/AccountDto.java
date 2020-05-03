package net.javaci.bank.api.dto;

import java.math.BigDecimal;
import java.util.Currency;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.NumberFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AccountDto {

    @NotEmpty
    @ApiModelProperty(position = 1, required = true,  value = "Account name", example = "Investment")
    private String accountName;

    @ApiModelProperty(position = 2, required = false,  value = "Account description", example = "Investment account")
    private String description;

    @NotEmpty
    @ApiModelProperty(position = 3, required = true,  value = "Account type", example = "INVEST", allowableValues="NORMAL, INVEST")
    private String accountType;

    @NotEmpty @Size(min = 3, max = 3)
    @ApiModelProperty(position = 4, required = true,  value = "Account type", example = "USD")
    private Currency currency;

    @NotEmpty
    @ApiModelProperty(position = 5, required = true,  value = "Account owner - customer id", example = "987654")
    private Long customer;

    @NumberFormat
    @ApiModelProperty(position = 6, required = false,  value = "Account balance", example = "0")
    private BigDecimal balance;

}
