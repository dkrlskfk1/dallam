package com.dallam.backend.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dallam.backend.dto.request.GatheringsCancelRequest;
import com.dallam.backend.dto.request.GatheringsCreateRequest;
import com.dallam.backend.dto.request.GatheringsSelectListRequest;
import com.dallam.backend.dto.request.GatheringsjoinedListRequest;
import com.dallam.backend.dto.request.PaticipantsRequest;
import com.dallam.backend.dto.response.GatheringDetailResponse;
import com.dallam.backend.dto.response.GatheringsCancelResponse;
import com.dallam.backend.dto.response.GatheringsCreateResponse;
import com.dallam.backend.dto.response.GatheringsSelectListResponse;
import com.dallam.backend.dto.response.GatheringsjoinedListResponse;

@Repository
public class GatheringsDao {

	@Autowired
    private SqlSessionTemplate sqlSession;

	public int createGathering(GatheringsCreateRequest gatheringsCreateRequest) {
		sqlSession.insert("gatherings.createGathering", gatheringsCreateRequest);
		return sqlSession.selectOne("gatherings.meetingId", gatheringsCreateRequest);
	}

	public void createuserMeeting(GatheringsCreateRequest gatheringsCreateRequest) {
		sqlSession.insert("gatherings.createuserMeeting", gatheringsCreateRequest);
	}

	public void createMeetingImage(Map<String, Object> meetingImage) {
		sqlSession.insert("gatherings.createMeetingImage", meetingImage);
	}

	public GatheringsCreateResponse meetings(GatheringsCreateRequest gatheringsCreateRequest) {
		return sqlSession.selectOne("gatherings.meetings", gatheringsCreateRequest);
	}

	public List<GatheringsSelectListResponse> getGatheringsList(GatheringsSelectListRequest request) {
		return sqlSession.selectList("gatherings.gatheringsSelectList", request);
	}

	public List<GatheringsjoinedListResponse> gatheringsjoinedList(GatheringsjoinedListRequest request) {
		return sqlSession.selectList("gatherings.gatheringsjoinedList", request);
	}

	public GatheringDetailResponse gatheringsById(int id) {
		return sqlSession.selectOne("gatherings.gatheringsById", id);
	}

	public int gatheringsByIdCheck(int id) {
        return sqlSession.selectOne("gatherings.gatheringsByIdCheck", id);
    }

	public List<Map<String, Object>> getParticipants(PaticipantsRequest request) {
        return sqlSession.selectList("gatherings.findParticipants", request);
	}

	public boolean authority(int id, String email) {
		Map<String, Object> params = new HashMap<>();
	    params.put("meetingId", id);
	    params.put("email", email);
		Integer count = sqlSession.selectOne("gatherings.authority", params);
        return count != null && count > 0;
    }

	public int meetingCancel(int id, String email) {
		Map<String, Object> params = new HashMap<>();
	    params.put("meetingId", id);
	    params.put("email", email);
	    return sqlSession.update("gatherings.meetingCancel", params);
    }

	public int userCancel(int id, String email) {
		Map<String, Object> params = new HashMap<>();
	    params.put("meetingId", id);
	    params.put("email", email);
	    return sqlSession.update("gatherings.userCancel", params);
    }

	public GatheringsCancelResponse gatheringsCancel(int id, String email) {
		Map<String, Object> params = new HashMap<>();
	    params.put("meetingId", id);
	    params.put("email", email);
		return sqlSession.selectOne("gatherings.gatheringsCancel", params);
	}

	public int meetingCheck(int id) {
        return sqlSession.selectOne("gatherings.meetingCheck", id);
    }

	public void gatheringsJoin(GatheringsCancelRequest gc) {
        sqlSession.insert("gatherings.gatheringsJoin", gc);
    }

	public String userMeetingCheck(int id, String email) {
		Map<String, Object> params = new HashMap<>();
	    params.put("meetingId", id);
	    params.put("email", email);
        return sqlSession.selectOne("gatherings.userMeetingCheck", params);
    }

	public boolean updateUserMeeting(int id, String email) {
		Map<String, Object> params = new HashMap<>();
	    params.put("meetingId", id);
	    params.put("email", email);
	    int result = sqlSession.update("gatherings.updateUserMeeting", params);
        return result > 0;
    }

	public boolean insertUserMeeting(int id, String email) {
		Map<String, Object> params = new HashMap<>();
	    params.put("meetingId", id);
	    params.put("email", email);
        int result = sqlSession.insert("gatherings.insertUserMeeting", params);
        return result > 0;
    }

	public boolean isParticipant(int meetingId, String email) {
		Map<String, Object> params = new HashMap<>();
	    params.put("meetingId", meetingId);
	    params.put("email", email);
		Integer count = sqlSession.selectOne("gatherings.isParticipant", params);
		return count != null && count > 0;
	}

	public boolean isPastGathering(int meetingId) {
	    Integer count = sqlSession.selectOne("gatherings.isPastGathering", meetingId);
	    return count != null && count > 0;
	}

	public boolean cancelParticipation(int meetingId, String email) {
	    Map<String, Object> params = new HashMap<>();
	    params.put("meetingId", meetingId);
	    params.put("email", email);
	    int result = sqlSession.update("gatherings.cancelParticipation", params);
	    return result > 0;
	}




}
