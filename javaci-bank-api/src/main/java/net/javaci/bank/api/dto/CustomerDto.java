package net.javaci.bank.api.dto;

import java.time.LocalDate;

import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CustomerDto {

    @ApiModelProperty(position = 1, required = true,  value = "Customer name", example = "Volkan")
    private String name;

    @ApiModelProperty(position = 2, required = true,  value = "Customer middle name", example = "Ozkan" )
    private String middleName;

    @ApiModelProperty(position = 3, required = true,  value = "Customer last name", example = "Gecici")
    private String lastName;

    @ApiModelProperty(position = 4, required = true,  value = "Customer address", example = "Buyukdere Caddesi No 5 Istanbul" )
    private String address;
    
    @ApiModelProperty(position = 5, required = true,  value = "Customer email", example = "janedoe@javaci.net" )
    private String email;
    
    @ApiModelProperty(position = 6, required = true,  value = "Customer phone number", example = "5491111111" )
    private String phoneNumber;
    
    @ApiModelProperty(position = 7, required = true,  value = "Customer identity no/citizenship id", example = "12345678901" )
    private String identityNo;

    @DateTimeFormat(pattern = "yyyy-MM-dd") @Size(min=10, max = 10)
    @ApiModelProperty(position = 8, required = true,  value = "Customer birth date in yyyy-MM-dd format", example = "1981-12-25")
    private LocalDate birthDate;
}
