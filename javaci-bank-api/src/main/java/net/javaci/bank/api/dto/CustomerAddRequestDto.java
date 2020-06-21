package net.javaci.bank.api.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
public class CustomerAddRequestDto extends CustomerDto {
    
    @NotEmpty	
    @ApiModelProperty(position = 201, required = true,  value = "password", example = "Ve4y.SeCu4e")
    private String password;
}
	
