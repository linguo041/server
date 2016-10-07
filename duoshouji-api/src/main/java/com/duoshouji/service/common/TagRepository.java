package com.duoshouji.service.common;

import java.util.List;

public interface TagRepository {

	List<Tag> listChannels();

	List<Tag> listTags(Category category, Brand brand);
	
	Tag findTag(long tagId);
	
	List<Tag> findTags(long[] tagIds);
}

