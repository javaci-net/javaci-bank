package net.javaci.bank.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserPassAuthRequest {

    private String username;

    private String password;

}
