package com.dallam.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dallam.backend.dto.request.ReviewsCreateRequest;
import com.dallam.backend.dto.request.ReviewsListRequest;
import com.dallam.backend.dto.request.ReviewsScoresList;
import com.dallam.backend.dto.response.ReviewsCreateResponse;
import com.dallam.backend.dto.response.ReviewsListResponse;
import com.dallam.backend.dto.response.ReviewsScoresListResponse;
import com.dallam.backend.exception.ValidationException;
import com.dallam.backend.service.GatheringsService;
import com.dallam.backend.service.ReviewsService;
import com.dallam.backend.util.formatter.DataResponseBodyFormatter;
import com.dallam.backend.util.formatter.ErrorCode;
import com.dallam.backend.util.formatter.ResponseBodyFormatter;
import com.dallam.backend.util.formatter.SuccessCode;

@RestController
@RequestMapping("/reviews")
public class ReviewsController {

	@Autowired
	private ReviewsService reviewsService;

	@Autowired
	private GatheringsService gatheringsService;

	// 리뷰 추가
	@PostMapping
	@Transactional(rollbackFor = Exception.class)
	public ResponseEntity<?> reviewsCreate(@RequestHeader("Authorization") String token, @RequestBody ReviewsCreateRequest request){
		try {
			String email = SecurityContextHolder.getContext().getAuthentication().getName();
			// 모임이 존재하는지 확인
			if(!gatheringsService.gatheringsByIdCheck(request.getGatheringId())) {
				return ResponseBodyFormatter.init(ErrorCode.NOT_FOUND, "모임을 찾을 수 없습니다", null);
			}

			// 모임에 참석했는지 확인
			if(!reviewsService.gatheringsParticipation(request.getGatheringId(), email)) {
				return ResponseBodyFormatter.init(ErrorCode.NOT_FOUND, "모임에 참석하지 않았습니다", null);
			}

			// 리뷰를 작성한 적이 있는지 확인
			if(reviewsService.reviewCreateCheck(request,email)) {
				return ResponseBodyFormatter.init(ErrorCode.REVIEW_ERROR, "이미 작성한 리뷰가 존재합니다.", null);
			}

			// 리뷰 추가
			ReviewsCreateResponse response = reviewsService.reviewsCreate(request,email);

			if(response.getComments() != null || !response.getComments().isEmpty()) {
				response.setComment(response.getComments());
			}

			return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "리뷰 추가 성공", response);

		}catch(Exception e) {
			e.printStackTrace();
			return ResponseBodyFormatter.init(ErrorCode.INTERNAL_ERROR, "서버 오류가 발생했습니다.", null);
		}
	}

	// 리뷰 목록 조회
	@GetMapping
	public ResponseEntity<?> reviewsList(@ModelAttribute ReviewsListRequest request){
		try {
			List<ReviewsListResponse> reviews = reviewsService.getReviewsList(request);
			return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "리뷰 목록 조회 성공", reviews);
		} catch (ValidationException e) {
			return ResponseEntity.badRequest().body(Map.of(
	            "code", "VALIDATION_ERROR",
	            "errors", e.getErrors()
	        ));
        } catch(Exception e) {
			e.printStackTrace();
			return ResponseBodyFormatter.init(ErrorCode.INTERNAL_ERROR, "리뷰 목록 조회 실패", null);
		}
	}

	// 리뷰 평점 목록 조회
	@GetMapping("/scores")
	public ResponseEntity<?> reviewsScoresList(@ModelAttribute ReviewsScoresList request){
		try {
			List<ReviewsScoresListResponse> reviews = reviewsService.reviewsScoresList(request);
			return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "리뷰 평점 목록 조회 성공", reviews);
		} catch (ValidationException e) {
			return ResponseEntity.badRequest().body(Map.of(
	            "code", "VALIDATION_ERROR",
	            "errors", e.getErrors()
	        ));
        } catch(Exception e) {
			e.printStackTrace();
			return ResponseBodyFormatter.init(ErrorCode.INTERNAL_ERROR, "리뷰 목록 조회 실패", null);
		}
	}

}
