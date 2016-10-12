package com.duoshouji.server.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = {"noteId"})
@Entity(name = "note")
@Table(name = "note")
//@org.hibernate.annotations.Table(indexes =
//	{
//		@Index(name = "notes_user_id_idx", columnNames = "user_id")
//	}, appliesTo = "note")
//@org.hibernate.annotations.Cache(region = "note", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DBNote {

	@Id
	@Column(name="note_id", columnDefinition="decimal(13,0)")
	private Long noteId;
	@Column(name="title", columnDefinition="varchar(50)")
	private String title;
	@Column(name="content", columnDefinition="varchar(500)")
	private String content;
	@Column(name="user_id", columnDefinition="decimal(11,0)")
	private Long userId;
	@Column(name="keyword", columnDefinition="varchar(500)")
	private String keyword;
	@Column(name="map_point", columnDefinition="point")
	private String mapPoint;
	@Column(name="map_point_string", columnDefinition="varchar(45)")
	private String mapPointS;
	@Column(name="address", columnDefinition="varchar(255)")
	private String address;
	@Column(name="country_id", columnDefinition="int(11)")
	private Long countryId;
	@Column(name="tag_id1", columnDefinition="decimal(13,0)")
	private Long tagId1;
	@Column(name="tag_name1", columnDefinition="varchar(45)")
	private String tagName1;
	@Column(name="tag_id2", columnDefinition="decimal(13,0)")
	private Long tagId2;
	@Column(name="tag_name2", columnDefinition="varchar(45)")
	private String tagName2;
	@Column(name="tag_id3", columnDefinition="decimal(13,0)")
	private Long tagId3;
	@Column(name="tag_name3", columnDefinition="varchar(45)")
	private String tagName3;
	@Column(name="tag_id4", columnDefinition="decimal(13,0)")
	private Long tagId4;
	@Column(name="tag_name4", columnDefinition="varchar(45)")
	private String tagName4;
	@Column(name="tag_id5", columnDefinition="decimal(13,0)")
	private Long tagId5;
	@Column(name="tag_name5", columnDefinition="varchar(45)")
	private String tagName5;
	@Column(name="tag_id6", columnDefinition="decimal(13,0)")
	private Long tagId6;
	@Column(name="tag_name6", columnDefinition="varchar(45)")
	private String tagName6;
	@Column(name="tag_id7", columnDefinition="decimal(13,0)")
	private Long tagId7;
	@Column(name="tag_name7", columnDefinition="varchar(45)")
	private String tagName7;
	@Column(name="tag_id8", columnDefinition="decimal(13,0)")
	private Long tagId8;
	@Column(name="tag_name8", columnDefinition="varchar(45)")
	private String tagName8;
	@Column(name="tag_id9", columnDefinition="decimal(13,0)")
	private Long tagId9;
	@Column(name="tag_name9", columnDefinition="varchar(45)")
	private String tagName9;
	@Column(name="is_suggest", columnDefinition="bit(1)")
	private Boolean suggest;
//	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="create_time", columnDefinition="bigint(20)")
	private Long createTime;
//	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_update_time", columnDefinition="bigint(20)")
	private Long lastUpdateTime;
	@Column(name="main_image_width", columnDefinition="int(11)")
	private Long mainImageWidth;
	@Column(name="main_image_height", columnDefinition="int(11)")
	private Long mainImageHeight;
	@Column(name="category_id", columnDefinition="decimal(13,0)")
	private Long categoryId;
	@Column(name="brand_id", columnDefinition="decimal(13,0)")
	private Long brandId;
	@Column(name="product_name", columnDefinition="varchar(255)")
	private String productName;
	@Column(name="longitude", columnDefinition="decimal(12,6)")
	private Double longitude;
	@Column(name="latitude", columnDefinition="decimal(12,6)")
	private Double latitude;
	@Column(name="price", columnDefinition="decimal(18,2)")
	private Double price;
	@Column(name="district_id", columnDefinition="decimal(12,0)")
	private Long districtId;
	@Column(name="main_image_url", columnDefinition="varchar(255)")
	private String mainImageUrl;
	@Column(name="rating", columnDefinition="int(11)")
	private Long rating;
	
	
}
