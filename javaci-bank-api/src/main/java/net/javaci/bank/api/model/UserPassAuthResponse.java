package net.javaci.bank.api.model;

import org.springframework.security.core.Authentication;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "User JWT Authentication response")
@Getter @Setter @AllArgsConstructor
public class UserPassAuthResponse {

	@ApiModelProperty(
			notes = "Authentication result",
			position = 1)
    private Authentication authResult;

	@ApiModelProperty(
			notes = "JWT Token", 
			example = "Bearer eyJhbGciOi..", 
			position = 2)
    private String jwtToken;

}
