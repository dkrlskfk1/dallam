package com.dallam.backend.dto.request;

import lombok.Data;

@Data
public class ReviewsCreateRequest {
	private int gatheringId;	// 모임 ID
	private int score;			// 평점
	private String comment;		// 리뷰

}
