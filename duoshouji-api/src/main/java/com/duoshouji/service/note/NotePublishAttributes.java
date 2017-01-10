package com.duoshouji.service.note;

import com.duoshouji.service.util.Location;


public interface NotePublishAttributes {

	public static final int MAX_TAG_COUNT = 9;
	
	public boolean isAddressSet();
	
	public String getAddress();
	
	public boolean isLocationSet();
	
	public Location getLocation();
	
	public int getRating();

	public String getTitle();

	public String getContent();

}
