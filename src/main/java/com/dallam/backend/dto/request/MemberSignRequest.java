package com.dallam.backend.dto.request;

import java.util.Date;

import lombok.Data;

@Data
public class MemberSignRequest {
	private String email;
    private String password;
    private String name;
    private String companyName;
    private Date createAt;
    private Date updateAt;

    public boolean isEmailValid() {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return this.email != null && this.email.matches(emailRegex);
    }
}
