package net.javaci.bank.api.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter @Setter
public class CustomerDto extends UserBaseDto {
   
	@NotEmpty
    @ApiModelProperty(position = 9, required = true,  value = "status", example = "UNAPPROVED", allowableValues="ACTIVE, INACTIVE, UNAPPROVED")
    private String status;
}
