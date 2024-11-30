package com.dallam.backend.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class GatheringsCreateRequest {
	private String email;                // 생성자
	private String location;             // 모임 장소
	private String type;                 // 모임 서비스 종류
	private String name;                 // 모임 이름
	private String dateTime;               // 모임 날짜 및 시간
	private int capacity;                // 모집 정원
	private List<MultipartFile> image;   // 모임 이미지
	private String registrationEnd;        // 모집 마감일 날짜 및 시간
	private int meetingId;
}
