package net.javaci.bank.api.model;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "User request data for JWT Authentication")
@Getter @Setter
public class UserPassAuthRequest {

	@ApiModelProperty(
			notes = "Username", 
			example = "a", 
			required = true, 
			position = 1)
	@JsonProperty(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY)
    private String username;

	@ApiModelProperty(
			notes = "Password", 
			example = "a", 
			required = true, 
			position = 2)
	@JsonProperty(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY)
    private String password;

}
