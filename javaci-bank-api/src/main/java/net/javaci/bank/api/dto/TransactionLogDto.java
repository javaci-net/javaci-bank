package net.javaci.bank.api.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotEmpty;

import org.springframework.format.annotation.NumberFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionLogDto {

	@NotEmpty
	@ApiModelProperty(position = 1, required = true, value = "Transactiom id", example = "987654")
	private Long id;
	
	@NotEmpty
	@ApiModelProperty(position = 3, required = true, value = "To customer name", example = "Ahmet Yilmaz")
	private String toCustomerName;
	
	@NotEmpty
	@ApiModelProperty(position = 2, required = true, value = "To Account id", example = "987654")
	private Long toAccountId;
	
	@NumberFormat
	@ApiModelProperty(position = 5, required = true, value = "Transaction amount", example = "0")
	private BigDecimal amount;

	@NotEmpty
	@ApiModelProperty(position = 6, required = true, value = "Transaction Type", example = "EXCHANGE", allowableValues = "EXCHANGE, TRANSFER")
	private String type;

	@ApiModelProperty(position = 4, required = true, value = "Transfer description", example = "Zengin oldun")
	private String description;

}
