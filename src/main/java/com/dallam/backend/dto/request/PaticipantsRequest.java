package com.dallam.backend.dto.request;

import lombok.Data;

@Data
public class PaticipantsRequest {
	private int id;							// 모임 id
	private int limit;						// 페이지당 참가자 수(Default value : 5)
	private int offset;						// 페이지 오프셋(Default value : 0)
	private String sortBy;					// 정렬 기준 (Available values : joinedAt , Default value : joinedAt)
	private String sortOrder;				// 정렬 순서 (Available values : asc, desc, Default value : asc)
}
