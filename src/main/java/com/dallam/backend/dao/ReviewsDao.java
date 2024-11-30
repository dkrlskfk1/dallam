package com.dallam.backend.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dallam.backend.dto.request.ReviewsCreateRequest;
import com.dallam.backend.dto.request.ReviewsListRequest;
import com.dallam.backend.dto.response.ReviewsCreateResponse;
import com.dallam.backend.dto.response.ReviewsListResponse;
import com.dallam.backend.dto.response.ReviewsScoresListResponse;

@Repository
public class ReviewsDao {

	@Autowired
    private SqlSessionTemplate sqlSession;

	public boolean gatheringsParticipation(int Id, String email) {
		Map<String, Object> params = new HashMap<>();
	    params.put("meetingId", Id);
	    params.put("email", email);
	    Integer count = sqlSession.selectOne("reviews.gatheringsParticipation", params);
	    return count != null && count > 0;
	}

	public boolean reviewCreateCheck(ReviewsCreateRequest request, String email) {
		Map<String, Object> params = new HashMap<>();
		params.put("meetingId", request.getGatheringId());
	    params.put("email", email);
	    Integer count = sqlSession.selectOne("reviews.reviewCreateCheck", params);
	    return count != null && count > 0;
	}

	public ReviewsCreateResponse reviewsCreate(ReviewsCreateRequest request, String email) {
		Map<String, Object> params = new HashMap<>();
	    params.put("meetingId", request.getGatheringId());
	    params.put("email", email);
	    params.put("score", request.getScore());
	    params.put("comment", request.getComment());
	    sqlSession.insert("reviews.reviewsCreate", params);
	    return sqlSession.selectOne("reviews.createReviews", params);
	}

	public List<ReviewsListResponse> getReviewsList(ReviewsListRequest request) {
        return sqlSession.selectList("reviews.getReviewsList", request);
    }

	public List<ReviewsScoresListResponse> reviewsScoresList(Map<String, Object> params) {
        return sqlSession.selectList("reviews.reviewsScoresList", params);
    }

}
