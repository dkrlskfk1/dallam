package com.dallam.backend.exception;

import java.util.List;
import java.util.Map;

public class ValidationException extends RuntimeException{
	private final List<Map<String, String>> errors;

    public ValidationException(List<Map<String, String>> errors) {
        super("Validation errors occurred"); // 기본 에러 메시지 설정
        this.errors = errors;
    }

    public List<Map<String, String>> getErrors() {
        return errors;
    }

}
