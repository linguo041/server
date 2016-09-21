package com.duoshouji.server.service.note.recommand;

import java.util.List;

import com.duoshouji.server.service.note.Note;

public interface NoteRecommendService {

	List<EcommerceItem> recommendEcommerceItems(Note note);
}
