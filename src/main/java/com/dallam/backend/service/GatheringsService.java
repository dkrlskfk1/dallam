package com.dallam.backend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dallam.backend.dao.GatheringsDao;
import com.dallam.backend.dto.request.GatheringsCreateRequest;
import com.dallam.backend.dto.request.GatheringsSelectListRequest;
import com.dallam.backend.dto.request.GatheringsjoinedListRequest;
import com.dallam.backend.dto.request.PaticipantsRequest;
import com.dallam.backend.dto.response.GatheringDetailResponse;
import com.dallam.backend.dto.response.GatheringsCancelResponse;
import com.dallam.backend.dto.response.GatheringsCreateResponse;
import com.dallam.backend.dto.response.GatheringsSelectListResponse;
import com.dallam.backend.dto.response.GatheringsjoinedListResponse;
import com.dallam.backend.dto.response.Participant;
import com.dallam.backend.dto.response.ParticipantsResponse;

@Service
public class GatheringsService {

	@Autowired
	private GatheringsDao gatheringsDao;

	@Autowired
	private S3Service s3Service;

	// 모임 생성
	@Transactional
    public GatheringsCreateResponse createGathering(GatheringsCreateRequest gatheringsCreateRequest) throws IOException {
    	// 이미지 파일 업로드 처리
        List<MultipartFile> images = gatheringsCreateRequest.getImage();
        StringBuilder imageUrls = new StringBuilder();

        List<String> uploadedImageUrls = new ArrayList<>();  // 업로드된 이미지 URL 저장용
        try {
        	// 모임 정보 저장
        	int meetingId = gatheringsDao.createGathering(gatheringsCreateRequest);

        	gatheringsCreateRequest.setMeetingId(meetingId);

        	gatheringsDao.createuserMeeting(gatheringsCreateRequest);
        	// 이미지 파일 각각을 S3에 업로드
        	if(images != null && !images.isEmpty()) {
        		for (MultipartFile image : images) {
        			String fileName = "dallam/gathering-images/" + image.getOriginalFilename();
        			String imageUrl = s3Service.uploadFile(image, fileName);  // S3Service를 사용하여 업로드

        			uploadedImageUrls.add(imageUrl);

        			Map<String, Object> meetingImage = new HashMap<>();
        			meetingImage.put("meetingId", meetingId);
        			meetingImage.put("imageName", image.getOriginalFilename());
        			meetingImage.put("imageUrl", imageUrl);

        			gatheringsDao.createMeetingImage(meetingImage);

        			imageUrls.append(imageUrl).append(",");
        		}
        	}

        	// 마지막 콤마 제거
        	if (imageUrls.length() > 0) {
        		imageUrls.setLength(imageUrls.length() - 1);
        	}

        	return gatheringsDao.meetings(gatheringsCreateRequest);

        } catch (Exception e) {
        	// 에러 발생시 S3 업로드된 파일 삭제
        	for(String imageUrl : uploadedImageUrls) {
        		s3Service.deleteFile(imageUrl);
        	}
        	throw e;
        }
	}

	@Transactional
    public List<GatheringsSelectListResponse> getGatheringsList(GatheringsSelectListRequest request) {
		// 조회할 모임수 검증
		if (request.getLimit() <= 0) {
			throw new IllegalArgumentException("limit");
		}

		// 모임 종류 검증
        if (request.getType() != null && !List.of("DALLAEMFIT", "OFFICE_STRETCHING", "MINDFULNESS", "WORKATION")
                .contains(request.getType())) {
            throw new IllegalArgumentException("type");
        }

        // 정렬 기준 검증
        if (request.getSortBy() != null && !List.of("dateTime", "registrationEnd", "participantCount")
                .contains(request.getSortBy())) {
            throw new IllegalArgumentException("sortBy");
        }

        // 정렬 순서 검증
        if (request.getSortOrder() != null && !List.of("asc", "desc").contains(request.getSortOrder())) {
            throw new IllegalArgumentException("sortOrder");
        }

        // 모임 위치 검증
        if (request.getLocation() != null && !List.of("건대입구", "을지로3가", "신림", "홍대입구").contains(request.getLocation())){
        	throw new IllegalArgumentException("location");
        }

        if(request.getId() != null && !request.getId().isEmpty()) {
        	List<String> idList = Arrays.asList(request.getId().split(","));
        	request.setIdList(idList);
        }

        return gatheringsDao.getGatheringsList(request);
    }

	@Transactional
    public List<GatheringsjoinedListResponse> gatheringsjoinedList(GatheringsjoinedListRequest request) {
		// 조회할 모임수 검증
		if (request.getLimit() <= 0) {
			throw new IllegalArgumentException("limit");
		}

        // 정렬 기준 검증
        if (request.getSortBy() != null && !List.of("dateTime", "registrationEnd", "participantCount")
                .contains(request.getSortBy())) {
            throw new IllegalArgumentException("sortBy");
        }

        // 정렬 순서 검증
        if (request.getSortOrder() != null && !List.of("asc", "desc").contains(request.getSortOrder())) {
            throw new IllegalArgumentException("sortOrder");
        }

        return gatheringsDao.gatheringsjoinedList(request);
    }

	public GatheringDetailResponse gatheringsById(int id) {
		return gatheringsDao.gatheringsById(id);
    }

	public boolean gatheringsByIdCheck(int id) {
		return gatheringsDao.gatheringsByIdCheck(id) > 0;
	}

	@Transactional
	public ParticipantsResponse getParticipants(PaticipantsRequest request) {
		// 기본값 처리
        if (request.getLimit() == 0) {
            request.setLimit(5); // Default limit
        }
        if (request.getOffset() == 0) {
            request.setOffset(0); // Default offset
        }
        if (request.getSortBy() == null) {
            request.setSortBy("joinedAt"); // Default sortBy
        }
        if (request.getSortOrder() == null) {
            request.setSortOrder("asc"); // Default sortOrder
        }
        List<Map<String, Object>> rawData = gatheringsDao.getParticipants(request);

        String userId = null; // 모임 주체자의 이름
        int gatheringId = request.getId(); // 모임 ID
        Date joinedAt = null; // 모임 참가 신청일
        List<Participant> participants = new ArrayList<>();

        for (Map<String, Object> row : rawData) {
            if (userId == null) {
                userId = String.valueOf(row.get("userId"));

                // Timestamp -> Date 변환
                Object joinedAtObject = row.get("joinedAt");

                if (joinedAtObject instanceof java.sql.Timestamp) {
                    joinedAt = new Date(((java.sql.Timestamp) joinedAtObject).getTime());
                } else if (joinedAtObject instanceof java.util.Date) {
                    joinedAt = (java.util.Date) joinedAtObject;
                }
            }

            participants.add(new Participant(
            	String.valueOf(row.get("participantNumber")),
            	String.valueOf(row.get("participantEmail")),
            	String.valueOf(row.get("participantName")),
            	String.valueOf(row.get("participantCompany")),
            	String.valueOf(row.get("participantImage"))
            ));
        }

        return new ParticipantsResponse(userId, gatheringId, joinedAt, participants);
	}

	public boolean authority(int id, String email) {
		return gatheringsDao.authority(id, email);
	}

	@Transactional
	public GatheringsCancelResponse gatheringsCancel(int id, String email) {
		// 모임 취소
		gatheringsDao.meetingCancel(id, email);

		// 모임에 참여한 모든 멤버들 참석 취소
		gatheringsDao.userCancel(id, email);

		// 조회
		return gatheringsDao.gatheringsCancel(id, email);
	}

	public boolean meetingCheck(int id) {
		boolean test = gatheringsDao.meetingCheck(id) > 0;
		return test;
	}

	public String gatheringsJoin(int id, String email) {
		return gatheringsDao.userMeetingCheck(id, email);
	}

	public boolean gatheringsJoinUpdate(int id, String email) {
		return gatheringsDao.updateUserMeeting(id, email);
	}

	public boolean gatheringsJoinInsert(int id, String email) {
		return gatheringsDao.insertUserMeeting(id, email);
	}

	public boolean validateParticipation(int meetingId, String email) {
        return gatheringsDao.isParticipant(meetingId, email);
    }

	public boolean isPastGathering(int meetingId) {
	    return gatheringsDao.isPastGathering(meetingId);
	}

	public boolean cancelParticipation(int meetingId, String email) {
	    return gatheringsDao.cancelParticipation(meetingId, email);
	}



}
