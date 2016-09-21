package com.duoshouji.server.internal.core.recommend;

import java.util.List;

import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.recommand.EcommerceItem;
import com.duoshouji.server.service.note.recommand.NoteRecommendService;

public abstract class KeywordBasedNoteRecommendService implements NoteRecommendService {

	@Override
	public List<EcommerceItem> recommendEcommerceItems(Note note) {
		return recommend(note.getProductName());
	}

	protected abstract List<EcommerceItem> recommend(String keyword);
}
