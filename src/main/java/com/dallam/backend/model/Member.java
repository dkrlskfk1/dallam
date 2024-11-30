package com.dallam.backend.model;

import java.util.Date;

import lombok.Data;

@Data
public class Member {
	private String email;
    private String password;
    private String name;
    private String companyName;
    private Date createAt;
    private Date updateAt;

}
