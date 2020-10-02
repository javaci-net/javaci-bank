package net.javaci.bank.api.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.annotations.ApiModelProperty;

@Getter @Setter @ToString
public abstract class UserBaseDto {

	@NotEmpty
    @ApiModelProperty(position = 1, required = true,  value = "citizen number", example = "12345678901" )
    private String citizenNumber;
    
	@NotEmpty
    @ApiModelProperty(position = 2, required = true,  value = "First name", example = "Volkan")
    private String firstName;

    @ApiModelProperty(position = 3, required = false,  value = "middle name", example = "Ozkan" )
    private String middleName;

	@NotEmpty
    @ApiModelProperty(position = 4, required = true,  value = "last name", example = "Gecici")
    private String lastName;
	
    @DateTimeFormat(pattern = "yyyy-MM-dd") @Size(min=10, max = 10)
    @ApiModelProperty(position = 5, required = true,  value = "birth date in yyyy-MM-dd format", example = "1981-12-25")
    private LocalDate birthDate;
    
	@NotEmpty
    @ApiModelProperty(position = 6, required = true,  value = "email", example = "janedoe@javaci.net" )
    private String email;
    
    @ApiModelProperty(position = 7, required = false,  value = "phone number", example = "5491111111" )
    private String phoneNumber;

}
