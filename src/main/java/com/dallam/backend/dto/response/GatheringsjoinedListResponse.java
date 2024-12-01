package com.dallam.backend.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GatheringsjoinedListResponse {
	private int id;						// 조회된 모임 ID
	private String type;				// 모임 종류
	private String name;				// 모임명
	private LocalDateTime dateTime;				// 모임 날짜(2024-11-13T11:50:38.978Z)
	private LocalDateTime registrationEnd;		// 모집 마감일(2024-11-13T11:50:38.978Z)
	private String location;			// 모임 위치
	private int participantCount;		// 모임 참가자 수
	private int capacity;				// 모집 정원
	private String image;				// 모임 이미지
	private int createdBy;				// 모임 생성자의 회원넘버
	private LocalDateTime canceledAt;			// 모임 취소일
	private LocalDateTime joinedAt;				// 모임 참여일
	private boolean isCompleted;		// 모임 참여완료 여부
	private boolean isReviewed;			// 리뷰작성여부

}
