package com.duoshouji.service.note.recommand;

import java.util.List;

import com.duoshouji.service.note.NoteDetail;

public interface NoteRecommendService {

	List<EcommerceItem> recommendEcommerceItems(NoteDetail note);
}
