package com.dallam.backend.dto.response;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GatheringDetailResponse {
	private int id;
	private String type;
	private String name;
	private LocalDateTime dateTime;
	private LocalDateTime registrationEnd;
	private String location;
	private int participantCount;
	private int capacity;
	private String image;
	private int createdBy;
	private Date canceledAt;
}
