package com.dallam.backend.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParticipantsResponse {
	private String userId; // MEMBER_NAME
    private int gatheringId; // MEETING_ID
    private LocalDateTime  joinedAt; // MEETING_JOINEDAT

    private List<Participant> user; // User 정보

}
