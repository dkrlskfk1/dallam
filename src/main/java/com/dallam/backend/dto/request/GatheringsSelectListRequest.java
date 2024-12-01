package com.dallam.backend.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class GatheringsSelectListRequest {
	private String id;			// ,로 구분된 모임 ID 목록
	private String type;		// 모임 종류(Available values : DALLAEMFIT, OFFICE_STRETCHING, MINDFULNESS, WORKATION)
	private String location;	// 모임 위치(Available values : 건대입구, 을지로3가, 신림, 홍대입구)
	private LocalDateTime date;		// 모임 날짜(YYYY-MM-DD 형식)
	private int createdBy;		// 모임 생성자(유저번호)
	private String sortBy;		// 정렬 기준(Available values : dateTime, registrationEnd, participantCount)
	private String sortOrder;	// 정렬 순서 (asc 또는 desc Available values : asc, desc)
	private int limit;			// 한번에 조회할 모임수 (최소 1)
	private int offset;			// 조회 시작 위치 (최소 0)
	private List<String> idList;
}
