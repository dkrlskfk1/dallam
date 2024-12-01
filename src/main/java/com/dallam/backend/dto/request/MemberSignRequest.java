package com.dallam.backend.dto.request;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MemberSignRequest {
	private String email;
    private String password;
    private String name;
    private String companyName;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public boolean isEmailValid() {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return this.email != null && this.email.matches(emailRegex);
    }
}
