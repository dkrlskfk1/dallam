package com.dallam.backend.dto.request;

import lombok.Data;

@Data
public class ReviewsListRequest {
	private Integer gatheringId;	// 모임 ID로 필터링
	private Integer userId;			// 사용자 ID로 필터링
	private String type;			// 모임 종류로 필터링 Available values : DALLAEMFIT, OFFICE_STRETCHING, MINDFULNESS, WORKATION
	private String location;		// 모임 위치로 필터링 Available values : 건대입구, 을지로3가, 신림, 홍대입구
	private String date;			// 모임 날짜로 필터링 (YYYY-MM-DD 형식)
	private String registrationEnd; // 모집 마감 날짜로 필터링 (YYYY-MM-DD 형식)
	private String sortBy;			// 정렬 기준 Available values : createdAt, score, participantCount
	private String sortOrder;		// 정렬 순서 (asc 또는 desc) Available values : asc, desc
	private Integer limit;			// 한 번에 조회할 리뷰 수 (최소 1)
	private Integer offset;			// 조회 시작 위치 (최소 0)

}
