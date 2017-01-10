package com.duoshouji.core;

import com.duoshouji.service.common.Tag;

public interface NoteFilter {

	Tag getChannel();
	
	boolean isChannelSet();
	
}
