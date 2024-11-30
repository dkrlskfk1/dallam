package com.dallam.backend.dto.request;

import lombok.Data;

@Data
public class GatheringsjoinedListRequest {
	private String email;
	private boolean completed;	// 모임 이용 완료 여부로 필터링(true일 경우 이용 완료한 모임만 조회)
	private boolean reviewed;	// 리뷰 작성 여부로 필터링(true일 경우 리뷰 작성한 모임만 조회)
	private int limit;			// 조회할 모임 수
	private int offset;			// 조회 시작 위치
	private String sortBy;		// 정렬 기준 (Available values : dateTime, registrationEnd, joinedAt
						// 			Default value : dateTime)

	private String sortOrder;	// 정렬 순서 Available values : asc, desc
						//		   Default value : asc

}
