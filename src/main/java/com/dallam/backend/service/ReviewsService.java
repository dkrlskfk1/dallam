package com.dallam.backend.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dallam.backend.dao.ReviewsDao;
import com.dallam.backend.dto.request.ReviewsCreateRequest;
import com.dallam.backend.dto.request.ReviewsListRequest;
import com.dallam.backend.dto.request.ReviewsScoresList;
import com.dallam.backend.dto.response.ReviewsCreateResponse;
import com.dallam.backend.dto.response.ReviewsListResponse;
import com.dallam.backend.dto.response.ReviewsScoresListResponse;
import com.dallam.backend.exception.ValidationException;


@Service
public class ReviewsService {

	@Autowired
	private ReviewsDao reviewsDao;

	public boolean gatheringsParticipation(int id, String email) {
		return reviewsDao.gatheringsParticipation(id, email);
	}

	public boolean reviewCreateCheck(ReviewsCreateRequest request, String email) {
		return reviewsDao.reviewCreateCheck(request, email);
	}

	public ReviewsCreateResponse reviewsCreate(ReviewsCreateRequest request, String email) {
		return reviewsDao.reviewsCreate(request, email);
	}

	public List<ReviewsListResponse> getReviewsList(ReviewsListRequest request) {
		if (request.getOffset() == null) {
			throw new ValidationException(List.of(
		        Map.of("parameter", "offset", "message", "최소 0 이상의 값을 입력해야 합니다.")
		    ));
		}
		if (request.getLimit() == null || request.getLimit() < 1) {
		    throw new ValidationException(List.of(
		        Map.of("parameter", "limit", "message", "최소 1 이상의 값을 입력해야 합니다.")
		    ));
		}

        return reviewsDao.getReviewsList(request);
    }

	public List<ReviewsScoresListResponse> reviewsScoresList(ReviewsScoresList request) {

		// gatheringId가 올바른 형식인지 검증
		List<String> normalizedGatheringIds = normalizeGatheringIds(request.getGatheringId());

		// DAO 호출
	    Map<String, Object> params = new HashMap<>();
	    params.put("gatheringIds", normalizedGatheringIds);
	    params.put("type", request.getType());

        return reviewsDao.reviewsScoresList(params);
    }

	// gatheringId 형식 검증
	private List<String> normalizeGatheringIds(String gatheringIds) {
	    // gatheringIds가 비어 있거나 null이면 그대로 반환
	    if (gatheringIds == null || gatheringIds.isBlank()) {
	        return Collections.emptyList();
	    }
	    // 쉼표가 없고 숫자만 있는 경우 단일 값으로 판단
	    if (!gatheringIds.contains(",") && gatheringIds.matches("^\\d+$")) {
	        return Collections.singletonList(gatheringIds.trim());
	    }
	    // 쉼표로 구분된 숫자 목록인지 확인
	    if (!gatheringIds.matches("^\\d+(,\\d+)*$")) {
	        throw new ValidationException(List.of(
	            Map.of(
	                "parameter", "gatheringId",
	                "message", "모임 ID는 쉼표로 구분된 숫자 목록이어야 합니다"
	            )
	        ));
	    }
	    return Arrays.stream(gatheringIds.split(","))
            .map(String::trim)
            .collect(Collectors.toList()); // 유효한 경우 그대로 반환
	}

}
