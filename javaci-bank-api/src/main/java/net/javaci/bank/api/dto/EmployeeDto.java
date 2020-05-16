package net.javaci.bank.api.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmployeeDto extends UserBaseDto {
	
	@NotEmpty
    @ApiModelProperty(position = 101, required = true,  value = "status", example = "INACTIVE", allowableValues="ACTIVE, INACTIVE")
    private String status;
}
