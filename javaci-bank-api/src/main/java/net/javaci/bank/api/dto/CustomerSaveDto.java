package net.javaci.bank.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class CustomerSaveDto extends CustomerBaseDto {
    
    @NotEmpty	
    @ApiModelProperty(position = 201, required = true,  value = "password", example = "Ve4y.SeCu4e")
    private String password;
}
	
