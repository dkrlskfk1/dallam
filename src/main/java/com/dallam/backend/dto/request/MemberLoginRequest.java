package com.dallam.backend.dto.request;

import lombok.Data;

@Data
public class MemberLoginRequest {
	private String email;
    private String password;
}
