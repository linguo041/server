package com.duoshouji.core.recommend;

import java.util.List;

import com.duoshouji.service.note.NoteDetail;
import com.duoshouji.service.note.recommand.EcommerceItem;
import com.duoshouji.service.note.recommand.NoteRecommendService;

public abstract class KeywordBasedNoteRecommendService implements NoteRecommendService {

	@Override
	public List<EcommerceItem> recommendEcommerceItems(NoteDetail note) {
		return recommend(note.getProductName());
	}

	protected abstract List<EcommerceItem> recommend(String keyword);
}
