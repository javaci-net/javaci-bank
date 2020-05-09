package net.javaci.bank.api.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotEmpty;

import org.springframework.format.annotation.NumberFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AccountDto {

    @NotEmpty
    @ApiModelProperty(position = 1, required = true,  value = "Account owner - customer id", example = "987654")
    private Long customer;
    
    @NotEmpty
    @ApiModelProperty(position = 2, required = true,  value = "Account name", example = "Investment")
    private String accountName;

    @ApiModelProperty(position = 3, required = false,  value = "Account description", example = "Investment account")
    private String description;

    @NumberFormat
    @ApiModelProperty(position = 4, required = false,  value = "Account balance", example = "0")
    private BigDecimal balance;
    
    @NotEmpty
    @ApiModelProperty(position = 5, required = true,  value = "Account Currency", example = "TL", allowableValues="TL, USD, EURO, GOLD")
    private String currency;

}
