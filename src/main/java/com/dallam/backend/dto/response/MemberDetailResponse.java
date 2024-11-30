package com.dallam.backend.dto.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberDetailResponse {
    private String email;
    private String name;
    private String companyName;
    private Date createdAt;
    private Date updatedAt;
    private String image;
    private int id;

}
