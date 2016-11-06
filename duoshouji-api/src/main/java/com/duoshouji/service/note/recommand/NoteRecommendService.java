package com.duoshouji.service.note.recommand;

import java.util.List;

import com.duoshouji.service.note.NoteTextProperties;

public interface NoteRecommendService {

	List<EcommerceItem> recommendEcommerceItems(NoteTextProperties note);
}
