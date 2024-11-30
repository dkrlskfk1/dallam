package com.dallam.backend.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dallam.backend.dto.request.GatheringsCreateRequest;
import com.dallam.backend.dto.request.GatheringsSelectListRequest;
import com.dallam.backend.dto.request.GatheringsjoinedListRequest;
import com.dallam.backend.dto.request.PaticipantsRequest;
import com.dallam.backend.dto.response.GatheringDetailResponse;
import com.dallam.backend.dto.response.GatheringsCancelResponse;
import com.dallam.backend.dto.response.GatheringsCreateResponse;
import com.dallam.backend.dto.response.GatheringsSelectListResponse;
import com.dallam.backend.dto.response.GatheringsjoinedListResponse;
import com.dallam.backend.dto.response.ParticipantsResponse;
import com.dallam.backend.service.GatheringsService;
import com.dallam.backend.util.formatter.DataResponseBodyFormatter;
import com.dallam.backend.util.formatter.ErrorCode;
import com.dallam.backend.util.formatter.ResponseBodyFormatter;
import com.dallam.backend.util.formatter.SuccessCode;

@RestController
@RequestMapping("/gatherings")
public class GatheringsController {

	@Autowired
	private GatheringsService gatheringsService;

	// 모임 생성
	@PostMapping
	public ResponseEntity<?> gatheringsCreate(@RequestHeader("Authorization") String token,@ModelAttribute GatheringsCreateRequest gatheringsCreateRequest) throws IOException{
		try {
			String email = SecurityContextHolder.getContext().getAuthentication().getName();
			gatheringsCreateRequest.setEmail(email);

			// 모임 생성 서비스 호출
            GatheringsCreateResponse gatheringsCreateResponse = gatheringsService.createGathering(gatheringsCreateRequest);

			return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "모임 생성 성공", gatheringsCreateResponse);
		} catch(IllegalArgumentException e) {
			return ResponseBodyFormatter.init(ErrorCode.VALIDATION_ERROR, "유효한 모임 종류를 입력하세요.", null);
		} catch (Exception e) {
            // 예기치 않은 오류 처리
			e.printStackTrace();
            return ResponseBodyFormatter.init(ErrorCode.INTERNAL_ERROR, "서버 오류가 발생했습니다.", null);
        }
	}

	// 모임조회
	@GetMapping
	public ResponseEntity<?> gatheringsList(@ModelAttribute GatheringsSelectListRequest request) {
		try {
			List<GatheringsSelectListResponse> gatherings = gatheringsService.getGatheringsList(request);
            return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "모임 목록 조회 성공", gatherings);
		} catch (IllegalArgumentException e) {
			String message;
			if ("limit".equals(e.getMessage())) {
	            message = "limit 1 이상이어야 합니다.";
	            return ResponseBodyFormatter.init(ErrorCode.VALIDATION_ERROR, message, request.getLimit());
	        } else if ("type".equals(e.getMessage())) {
	            message = "유효한 모임 종류를 입력하세요.";
	            return ResponseBodyFormatter.init(ErrorCode.VALIDATION_ERROR, message, request.getType());
	        } else if ("sortBy".equals(e.getMessage())) {
	            message = "유효한 정렬 기준을 입력하세요.";
	            return ResponseBodyFormatter.init(ErrorCode.VALIDATION_ERROR, message, request.getSortBy());
	        } else if ("sortOrder".equals(e.getMessage())) {
	            message = "유효한 정렬 순서를 입력하세요.";
	            return ResponseBodyFormatter.init(ErrorCode.VALIDATION_ERROR, message, request.getSortOrder());
	        } else if ("location".equals(e.getMessage())) {
	            message = "유효한 위치를 입력하세요.";
	            return ResponseBodyFormatter.init(ErrorCode.VALIDATION_ERROR, message, request.getLocation());
	        } else {
	        	e.printStackTrace();
	            return ResponseBodyFormatter.init(ErrorCode.INTERNAL_ERROR, "서버 오류가 발생했습니다.", null);
	        }

		}
	}

	// 모임 상세 조회
	@GetMapping("/{id}")
	public ResponseEntity<?> gatheringsById(@PathVariable("id") int id) {
		try {
			// 모임이 있는지 체크
			if(!gatheringsService.gatheringsByIdCheck(id)) {
				// 존재하지 않을 시
				return ResponseBodyFormatter.init(ErrorCode.NOT_FOUND, "모임을 찾을 수 없습니다", null);
			}

			// 모임이 존재하면
			GatheringDetailResponse gatherings = gatheringsService.gatheringsById(id);


			return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "참석한 모임 목록 조회 성공", gatherings);
		} catch (Exception e) {
			return ResponseBodyFormatter.init(ErrorCode.VALIDATION_ERROR, "유효한 모임 ID를 입력하세요", id);
		}
	}

	// 로그인된 사용자가 참석한 모임 목록 조회
	@GetMapping("/joined")
	public ResponseEntity<?> gatheringsjoinedList(@RequestHeader("Authorization") String token,@ModelAttribute GatheringsjoinedListRequest request) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		request.setEmail(email);
		try {
			List<GatheringsjoinedListResponse> gatherings = gatheringsService.gatheringsjoinedList(request);

			return ResponseEntity.ok(gatherings);
		} catch (IllegalArgumentException e) {
			return ResponseBodyFormatter.init(ErrorCode.VALIDATION_ERROR, "limit 1 이상이어야 합니다.", null);
		}
	}

	// 특정 모임의 참가자 목록 조회
	@GetMapping("/{id}/participants")
	public ResponseEntity<?> participants(@PathVariable("id") int id,@ModelAttribute PaticipantsRequest request){
		try {
			// 모임이 존재하는지 체크
			if(!gatheringsService.gatheringsByIdCheck(id)) {
				return ResponseBodyFormatter.init(ErrorCode.NOT_FOUND, "모임을 찾을 수 없습니다", null);
			}
			// 모임의 참가자 목록 조회
			ParticipantsResponse participants = gatheringsService.getParticipants(request);

			return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "참가자 목록 조회 성공", participants);
		} catch (Exception e) {
			e.printStackTrace();
			// 예상 밖의 에러 발생
			return ResponseBodyFormatter.init(ErrorCode.VALIDATION_ERROR, "유효한 모임 ID를 입력하세요", id);
		}
	}

	// 테스트 필요
	// 모임 취소
	@PutMapping("/{id}/cancel")
	public ResponseEntity<?> gatheringsCancel(@PathVariable("id") int id, @RequestHeader("Authorization") String token) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			// 모임이 존재하는지 체크
			if(!gatheringsService.gatheringsByIdCheck(id)) {
				return ResponseBodyFormatter.init(ErrorCode.NOT_FOUND, "모임을 찾을 수 없습니다", null);
			}

			// 모임 취소 권한 체크
			boolean exists = gatheringsService.authority(id, email);
			if(!exists) {
				return ResponseBodyFormatter.init(ErrorCode.UNAUTHORIZED, "모임을 취소할 권한이 없습니다", null);
			}

			// 모임 취소
			GatheringsCancelResponse response = gatheringsService.gatheringsCancel(id, email);
			return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "모임 취소 성공", response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBodyFormatter.init(ErrorCode.VALIDATION_ERROR, "유효한 모임 ID를 입력하세요", id);
		}

	}

	// 모임 참여
	@PostMapping("/{id}/join")
	public ResponseEntity<?> gatheringsJoin(@PathVariable("id") int id, @RequestHeader("Authorization") String token) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			// 모임이 존재하는지 체크
			if(!gatheringsService.gatheringsByIdCheck(id)) {
				return ResponseBodyFormatter.init(ErrorCode.NOT_FOUND, "모임을 찾을 수 없습니다", null);
			}

			// 취소된 모임인지 체크
			if(!gatheringsService.meetingCheck(id)) {
				return ResponseBodyFormatter.init(ErrorCode.GATHERING_CANCELED, "취소된 모임입니다", null);
			}

			// 모임 참여 여부 체크
			String check = gatheringsService.gatheringsJoin(id, email);

			if(check == null || check.isEmpty()) {
				// 참여 모임 추가
				boolean isMeeting = gatheringsService.gatheringsJoinInsert(id, email);

				if(isMeeting) {
					return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "모임에 참여했습니다.");
				}
			}

			if(check.equals("N")) {
				// 이미 참석했던 모임이면
				return ResponseBodyFormatter.init(ErrorCode.GATHERING_CANCELED, "이미 참석한 모임입니다.", null);
			}

			if(!check.isEmpty() && check.equals("Y")) {
				// 모임 참여 취소였던 모임일시
				boolean isMeeting = gatheringsService.gatheringsJoinUpdate(id, email);

				if(isMeeting) {
					return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "모임에 참여했습니다.");
				}
			}
			// 이외 예상치 못한 변수 발생시
			return ResponseBodyFormatter.init(ErrorCode.VALIDATION_ERROR, "유효한 모임 ID를 입력하세요", id);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBodyFormatter.init(ErrorCode.VALIDATION_ERROR, "유효한 모임 ID를 입력하세요", id);
		}

	}


	// 모임 참여 취소
	@DeleteMapping("{id}/leave")
	public ResponseEntity<?> gatheringsUserDelete(@PathVariable("id") int id, @RequestHeader("Authorization") String token) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			// 모임이 존재하는지 확인
			boolean exists = gatheringsService.validateParticipation(id, email);
			System.out.println(exists);
			if (!exists) {
				return ResponseBodyFormatter.init(ErrorCode.NOT_FOUND, "모임을 찾을 수 없습니다", null);
			}
			// 이미 지난 모임인지 확인
			boolean isPastGathering = gatheringsService.isPastGathering(id);
			if (isPastGathering) {
				return ResponseBodyFormatter.init(ErrorCode.VALIDATION_ERROR, "이미 지난 모임입니다", id);
			}

			// 참여 취소 처리
			boolean isCancelled = gatheringsService.cancelParticipation(id, email);
			if (isCancelled) {
				return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "모임을 참여 취소했습니다.");
			}

			// 이외의 변수 발생 시
			return ResponseBodyFormatter.init(ErrorCode.VALIDATION_ERROR, "유효한 모임 ID를 입력하세요", id);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBodyFormatter.init(ErrorCode.VALIDATION_ERROR, "유효한 모임 ID를 입력하세요", id);
		}


	}
}
