package com.dallam.backend.dto.response;

import java.util.Date;

import lombok.Data;

@Data
public class ReviewsListResponse {
	private Integer id;
	private Integer score;
	private String comment;
	private Date createdAt;

	private Gathering gathering;
	private User user;

	@Data
	public static class Gathering {
		private Integer id;
		private String type;
		private String name;
		private Date dateTime;
		private String location;
		private String image;
	}

	@Data
    public static class User {
        private Integer id;
        private String name;
        private String image;
    }

}
