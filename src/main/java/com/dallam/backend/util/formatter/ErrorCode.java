package com.dallam.backend.util.formatter;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode implements ResponseCode {

	INVALID_EMAIL(HttpStatus.BAD_REQUEST, "유효하지 않은 이메일입니다."),
	DUPLICATE_EMAIL(HttpStatus.CONFLICT, "중복된 이메일입니다."),
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류"),
	INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "잘못된 이메일 또는 비밀번호입니다"),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),
	NOT_FOUND(HttpStatus.NOT_FOUND, "모임을 찾을 수 없습니다"),
	UPDATE_FAILED(HttpStatus.BAD_REQUEST, "프로필 업데이트 실패"),
	VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "유효한 모임 종류를 입력하세요"),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Authorization 헤더가 필요합니다"),
	GATHERING_CANCELED(HttpStatus.BAD_REQUEST, "취소된 모임입니다."),
	PAST_GATHERING(HttpStatus.BAD_REQUEST, "이미 지난 모임입니다."),
	FORBIDDEN(HttpStatus.BAD_REQUEST, "모임에 참석하지 않았습니다."),
	REVIEW_ERROR(HttpStatus.BAD_REQUEST, "이미 작성한 리뷰가 존재합니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }


	@Override
	public String getCode() {
        return name();
    }

}
