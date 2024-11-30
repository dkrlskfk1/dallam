package com.dallam.backend.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dallam.backend.dao.MemberDao;
import com.dallam.backend.dto.request.MemberLoginRequest;
import com.dallam.backend.dto.request.MemberProfileUpdateRequest;
import com.dallam.backend.dto.request.MemberSignRequest;
import com.dallam.backend.dto.response.MemberDetailResponse;
import com.dallam.backend.model.Member;

@Service
public class MemberService {

	@Autowired
	private MemberDao memberDao;

	@Autowired
    private PasswordEncoder passwordEncoder;

	@Autowired
	private S3Service s3Service;

	public boolean isEmailExists(String email) {
		return memberDao.checkEmailExists(email) > 0;
	}

	public void registerMember(MemberSignRequest member) {
		// 비밀번호 암호화
		String encryptedPassword = passwordEncoder.encode(member.getPassword());
		member.setPassword(encryptedPassword);

		memberDao.insertMember(member);
	}

	public boolean authenticate(MemberLoginRequest login) {
		Member member = memberDao.findByEmail(login);

		// 비밀번호 비교
		if(member != null && passwordEncoder.matches(login.getPassword(), member.getPassword())) {
			return true;
		}
		return false;
	}

    public MemberDetailResponse getMemberInfo(String email) {
    	MemberDetailResponse member = memberDao.getMemberInfo(email);
    	return member;
    }

    @Transactional
    public boolean updateProfile(String email, MemberProfileUpdateRequest request) throws IOException {
    	String s3imageUrl = null;	// 업로드 될 이미지 URL 저장용
    	try {
    		// 법인명 업데이트
    		if(request.getCompanyName() != null) {
    			Map<String, Object> memberUpdate = new HashMap<>();
    			memberUpdate.put("email", email);
    			memberUpdate.put("companyName", request.getCompanyName());
    			memberDao.updateMember(memberUpdate);
    		}

    		// 프로필 이미지 업데이트
    		String folderPath = "dallam/";
    		String existingImageUrl = memberDao.findByImageUrl(email); // 프로필 이미지 경로 조회

    		// 프로필 이미지가 존재시 삭제
    		if(request.getImage() != null && existingImageUrl != null) {
    			s3Service.deleteFile(existingImageUrl);
    		}

    		// S3에 새로운 이미지 업로드
    		if (request.getImage() != null) {
    			String filePath = folderPath + email + "/" + request.getImage().getOriginalFilename();
    			s3imageUrl = s3Service.uploadFile(request.getImage(), filePath);

    			Map<String, Object> memberImage = new HashMap<>();
    			String imageName = request.getImage().getOriginalFilename();
    			memberImage.put("imageName", imageName);	// 파일명
    			memberImage.put("imageUrl", filePath);	// S3 파일경로
    			memberImage.put("email", email);	// 이메일

    			// DB에 새로운 이미지 URL 저장 (이미지 URL이 없으면 INSERT)
    			if (existingImageUrl == null) {
    				memberDao.insertImage(memberImage);  // 이미지 URL 새로 INSERT
    			} else {
    				memberDao.updateImage(memberImage);  // 기존 이미지 URL 업데이트
    			}
    		}
    		return true;	// 프로필 업데이트 성공

    	} catch (IOException e) {	// 파일 처리 오류
    		// 에러 발생시 S3 업로드된 파일 삭제
        	if(!s3imageUrl.isEmpty()) {
        		s3Service.deleteFile(s3imageUrl);
        	}
    		e.printStackTrace();
    		return false;

    	} catch (Exception e) {	// 일반 예외 처리
    		e.printStackTrace();
    		return false;
    	}
    }





}
