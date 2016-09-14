package com.duoshouji.server.service.common;

import java.util.List;

public interface TagRepository {

	List<Tag> listChannels();

	List<Tag> listTags();
	
	Tag findTag(long tagId);
	
	List<Tag> findTags(long[] tagIds);
}

