package com.dallam.backend.dto.response;

import java.util.Date;

import lombok.Data;

@Data
public class ReviewsCreateResponse {
	private int id;
	private String userId;
	private int gatheringId;
	private int score;
	private String comment;
	private String comments;
	private Date createdAt;
}
