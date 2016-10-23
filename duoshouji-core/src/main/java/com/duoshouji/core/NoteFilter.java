package com.duoshouji.core;

import com.duoshouji.service.common.Tag;
import com.duoshouji.service.util.Location;

public interface NoteFilter {

	Tag getChannel();
	
	boolean isChannelSet();
	
	Location getLocation();
	
	boolean isLocationSet();
	
}
