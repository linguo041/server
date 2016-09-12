package com.duoshouji.server.service.note;

import java.math.BigDecimal;
import java.util.List;

import com.duoshouji.server.service.common.Brand;
import com.duoshouji.server.service.common.Category;
import com.duoshouji.server.service.common.District;
import com.duoshouji.server.service.common.Tag;
import com.duoshouji.server.util.Location;

public interface NotePublishAttributes {

	public static final int MAX_TAG_COUNT = 9;
	
	public Category getCategory();

	public Brand getBrand();

	public String getProductName();

	public District getDistrict();

	public BigDecimal getPrice();

	public int getRating();

	public Location getLocation();

	public String getTitle();

	public String getContent();

	public List<Tag> getTags();
	
	public int getTagCount();

}
