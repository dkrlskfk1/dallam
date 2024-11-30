package com.dallam.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dallam.backend.dto.request.MemberLoginRequest;
import com.dallam.backend.dto.request.MemberProfileUpdateRequest;
import com.dallam.backend.dto.request.MemberSignRequest;
import com.dallam.backend.dto.response.MemberDetailResponse;
import com.dallam.backend.dto.response.MemberLoginResponse;
import com.dallam.backend.service.MemberService;
import com.dallam.backend.util.formatter.DataResponseBodyFormatter;
import com.dallam.backend.util.formatter.ErrorCode;
import com.dallam.backend.util.formatter.ResponseBodyFormatter;
import com.dallam.backend.util.formatter.SuccessCode;
import com.dallam.backend.util.jwt.JwtUtil;

@RestController
@RequestMapping("/auths")
public class MemberController {

	@Autowired
	private MemberService memberService;

	@Autowired
    private JwtUtil jwtUtil;

	// 회원가입
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody MemberSignRequest member){
		if (!member.isEmailValid()) {
            return ResponseBodyFormatter.init(ErrorCode.INVALID_EMAIL, "유효한 이메일 주소를 입력하세요.", member.getEmail());
        }

		// 이메일 중복 검사
        if (memberService.isEmailExists(member.getEmail())) {
            return ResponseBodyFormatter.init(ErrorCode.INVALID_EMAIL, "이미 사용 중인 이메일입니다.", member.getEmail());
        }

        // 회원 등록 로직 호출
        try {
            memberService.registerMember(member);
            return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "가입 성공", null);
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseBodyFormatter.init(ErrorCode.INTERNAL_ERROR, "error", "회원 가입 중 오류가 발생했습니다.");
        }
	}

	// 로그인
	@PostMapping("signin")
	public ResponseEntity<?> signin(@RequestBody MemberLoginRequest login){
		if(memberService.authenticate(login)) {
			String token = jwtUtil.generateToken((login.getEmail()));
			MemberLoginResponse memberLoginResponse = new MemberLoginResponse(token);
			return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "로그인 성공", memberLoginResponse);
		}else {
			return ResponseBodyFormatter.init(ErrorCode.INVALID_CREDENTIALS, "로그인 실패: 이메일 또는 비밀번호가 일치하지 않습니다.", null);
		}
	}

	// 로그아웃
	@PostMapping("signout")
	public ResponseEntity<?> signout(@RequestHeader("Authorization") String token) {
        // 토큰이 유효하고 블랙리스트에 추가되지 않았다면 로그아웃 처리합니다.
        if (token != null && !jwtUtil.isTokenBlacklisted(token)) {
            jwtUtil.addToBlacklist(token);
            SecurityContextHolder.clearContext();
            return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "로그아웃되었습니다.");
        } else {
            // 유효하지 않은 토큰이거나 이미 블랙리스트에 추가된 경우 처리
            return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "로그아웃 중 오류가 발생하였습니다.");
        }

    }

	// 회원 정보 확인
	@GetMapping("user")
	public ResponseEntity<?> getMyInfo(@RequestHeader("Authorization") String token) {
		try {
			String email = SecurityContextHolder.getContext().getAuthentication().getName();

			// 회원 정보 조회
			MemberDetailResponse memberDetail = memberService.getMemberInfo(email);

			// 회원 정보가 없으면
			if(memberDetail.getEmail() == null) {
				return ResponseBodyFormatter.init(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다", null);
			}

			// 회원 정보가 있으면
			return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, "회원 정보 조회 성공", memberDetail);
		}catch (Exception e) {
			e.printStackTrace();  // 예외 로그 출력
            return ResponseBodyFormatter.init(ErrorCode.INTERNAL_ERROR, "내부 서버 오류", null);
        }
    }

	// 회원 정보 수정
	@PutMapping(value = "/user", consumes = "multipart/form-data")
	public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String token,
											@RequestPart("companyName") String companyName,
									        @RequestPart("image") MultipartFile image)	{
		try {
            // 회원 이메일 가져오기
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            MemberProfileUpdateRequest updateRequest = new MemberProfileUpdateRequest();
            updateRequest.setCompanyName(companyName);
            updateRequest.setImage(image);

            // 프로필 업데이트 처리
            boolean isUpdated = memberService.updateProfile(email, updateRequest);

            if (isUpdated) {
            	// 업데이트가 완료되면
            	MemberDetailResponse memberDetail = memberService.getMemberInfo(email);
                return ResponseBodyFormatter.init(SuccessCode.SUCCESS, "프로필 업데이트 성공", memberDetail);
            } else {
                return ResponseBodyFormatter.init(ErrorCode.UPDATE_FAILED, "프로필 업데이트 실패", null);
            }
		} catch (Exception e) {
            e.printStackTrace();  // 예외 로그 출력
            return ResponseBodyFormatter.init(ErrorCode.INTERNAL_ERROR, "내부 서버 오류", null);
        }

	}

}
