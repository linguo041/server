package com.duoshouji.server.service.note;

import com.duoshouji.server.util.Location;

public interface CommentPublishAttributes {

	String getComment();
	
	int getRating();
	
	Location getLocation();
}
