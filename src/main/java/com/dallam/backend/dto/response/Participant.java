package com.dallam.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Participant {
	private String id; // MEETING_NUMBER
    private String email; // MEMBER_EMAIL
    private String name; // MEMBER_NAME
    private String companyName; // COMPANY_NAME
    private String image; // IMAGE_URL
}
