package net.javaci.bank.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class UserBaseDto {

    @ApiModelProperty(position = 1, required = true,  value = "Customer name", example = "Volkan")
    private String name;

    @ApiModelProperty(position = 2, required = true,  value = "Customer middle name", example = "Ozkan" )
    private String middleName;

    @ApiModelProperty(position = 3, required = true,  value = "Customer last name", example = "Gecici")
    private String lastName;
    
    @ApiModelProperty(position = 4, required = true,  value = "Customer email", example = "janedoe@javaci.net" )
    private String email;
    
    @ApiModelProperty(position = 5, required = true,  value = "Customer phone number", example = "5491111111" )
    private String phoneNumber;
    
    @ApiModelProperty(position = 6, required = true,  value = "Customer identity no/citizenship id", example = "12345678901" )
    private String identityNo;
    
    @ApiModelProperty(position = 7, required = true,  value = "Customer password", example = "Ve4y.SeCu4e")
    private String password;
    
    public String getPassword() {
        return "******";
    }

}
