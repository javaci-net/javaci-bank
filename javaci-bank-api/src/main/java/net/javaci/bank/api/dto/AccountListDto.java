package net.javaci.bank.api.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AccountListDto extends AccountSaveDto {
	
	@NotEmpty
	@ApiModelProperty(position = 101, required = true, value = "Account id", example = "987654")
	private Long id;

	@NotEmpty
    @ApiModelProperty(position = 102, required = true,  value = "Account Number", example = "123456-00002")
    private String accountNumber;

}
