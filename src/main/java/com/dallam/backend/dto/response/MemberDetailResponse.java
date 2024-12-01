package com.dallam.backend.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberDetailResponse {
    private String email;
    private String name;
    private String companyName;
    private LocalDateTime  createdAt;
    private LocalDateTime  updatedAt;
    private String image;
    private int id;

}
