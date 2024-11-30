package com.dallam.backend.dto.request;

import lombok.Data;

@Data
public class ReviewsScoresList {
	private String gatheringId;	// 모임 ID의 쉼표로 구분된 목록으로 필터링
	private String type;		// 모임 종류로 필터링 Available values : DALLAEMFIT, OFFICE_STRETCHING, MINDFULNESS, WORKATION

}
