package net.javaci.bank.api.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AccountWithIdDto extends AccountDto{

    @NotEmpty
    @ApiModelProperty(position = 0, required = true,  value = "Account id", example = "987654")
    private Long id;

}
