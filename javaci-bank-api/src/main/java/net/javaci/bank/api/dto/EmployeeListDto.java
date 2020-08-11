package net.javaci.bank.api.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmployeeListDto extends EmployeeBaseDto {
    
	@NotEmpty
    @ApiModelProperty(position = 201, required = true,  value = "id", example = "1" )
    private Long id;
}
	
