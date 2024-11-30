package com.dallam.backend.dto.response;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParticipantsResponse {
	private String userId; // MEMBER_NAME
    private int gatheringId; // MEETING_ID
    private Date joinedAt; // MEETING_JOINEDAT

    private List<Participant> user; // User 정보

}
