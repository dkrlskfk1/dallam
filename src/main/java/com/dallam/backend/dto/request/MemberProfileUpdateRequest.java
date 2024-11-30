package com.dallam.backend.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class MemberProfileUpdateRequest {
	private String companyName;
    private MultipartFile image; // 프로필 이미지 파일
}
