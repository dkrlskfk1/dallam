package com.dallam.backend.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReviewsCreateResponse {
	private int id;
	private String userId;
	private int gatheringId;
	private int score;
	private String comment;
	private String comments;
	private LocalDateTime  createdAt;
}
