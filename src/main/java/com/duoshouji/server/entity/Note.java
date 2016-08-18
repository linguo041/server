package com.duoshouji.server.entity;

import java.util.Date;

import lombok.Data;

@Data
public class Note {
	private Long id;
	private String title;
	private String image1;
	private String image2;
	private String image3;
	private String image4;
	private String image5;
	private String image6;
	private String content;
	private Long userId;
	private String mapPoint;
	private String mapPointString;
	private String address;
	private Long contryId;
	private Long tagId1;
	private String tagName1;
	private Long tagId2;
	private String tagName2;
	private Long tagId3;
	private String tagName3;
	private Long tagId4;
	private String tagName4;
	private Long tagId5;
	private String tagName5;
	private Long tagId6;
	private String tagName6;
	private Long tagId7;
	private String tagName7;
	private Long tagId8;
	private String tagName8;
	private Long tagId9;
	private String tagName9;
	private Boolean isSuggest;
	private Date createdTime;
	private Date lastUpdateTime;
}
