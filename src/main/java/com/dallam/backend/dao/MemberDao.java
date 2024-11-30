package com.dallam.backend.dao;

import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dallam.backend.dto.request.MemberLoginRequest;
import com.dallam.backend.dto.request.MemberSignRequest;
import com.dallam.backend.dto.response.MemberDetailResponse;
import com.dallam.backend.model.Member;

@Repository
public class MemberDao {

	@Autowired
    private SqlSessionTemplate sqlSession;

    public int checkEmailExists(String email) {
        return sqlSession.selectOne("member.checkEmailExists", email);
    }

    public void insertMember(MemberSignRequest member) {
        sqlSession.insert("member.insertMember", member);
    }

    public Member findByEmail(MemberLoginRequest login) {
    	return sqlSession.selectOne("member.findByEmail", login);
    }

    public MemberDetailResponse getMemberInfo(String email) {
    	return sqlSession.selectOne("member.getMemberInfo", email);
    }

    public String findByImageUrl(String email) {
    	return sqlSession.selectOne("member.findByImageUrl", email);
    }

    public void updateMember(Map<String, Object> memberUpdate) {
        sqlSession.update("member.updateMember", memberUpdate);
    }

    public void insertImage(Map<String, Object> memberImage) {
        sqlSession.insert("member.insertImage", memberImage);
    }

    public void updateImage(Map<String, Object> memberImage) {
        sqlSession.update("member.updateImage", memberImage);
    }
}
