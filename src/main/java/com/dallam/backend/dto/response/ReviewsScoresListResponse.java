package com.dallam.backend.dto.response;

import lombok.Data;

@Data
public class ReviewsScoresListResponse {
	private Integer gatheringId;		// 모임 ID
	private String type;				// 모임 타입
	private Integer averageScore;
	private Integer oneStar;
	private Integer twoStars;
	private Integer threeStars;
	private Integer fourStars;
	private Integer fiveStars;

}
