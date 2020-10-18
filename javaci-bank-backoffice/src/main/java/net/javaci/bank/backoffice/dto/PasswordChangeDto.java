package net.javaci.bank.backoffice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PasswordChangeDto {
	
	private String currentPassword;
	
	private String newPassword;
	
	private String confirmNewPassword;

}
