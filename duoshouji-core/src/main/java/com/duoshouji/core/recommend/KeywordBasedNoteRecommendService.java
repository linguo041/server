package com.duoshouji.core.recommend;

import java.util.List;

import com.duoshouji.service.note.NoteTextProperties;
import com.duoshouji.service.note.recommand.EcommerceItem;
import com.duoshouji.service.note.recommand.NoteRecommendService;

public abstract class KeywordBasedNoteRecommendService implements NoteRecommendService {

	@Override
	public List<EcommerceItem> recommendEcommerceItems(NoteTextProperties noteProperties) {
		return recommend(noteProperties.getCommodity().getProductName());
	}

	protected abstract List<EcommerceItem> recommend(String keyword);
}
