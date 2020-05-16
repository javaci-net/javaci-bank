package net.javaci.bank.api.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmployeeAddRequestDto extends EmployeeDto {
    
    @NotEmpty	
    @ApiModelProperty(position = 201, required = true,  value = "password", example = "Ve4y.SeCu4e")
    private String password;
}
	
