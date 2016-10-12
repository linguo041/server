package com.duoshouji.server.elasticsearch;

import com.duoshouji.server.elasticsearch.document.ESNote;
import com.duoshouji.server.jpa.entity.DBNote;
import com.google.common.base.Function;

public class ESUtil {
	public static final Function<DBNote, ESNote> DBNOTE_TO_ES_FUNCTION = new Function<DBNote, ESNote>() {

		@Override
		public ESNote apply(DBNote input) {
			ESNote esNote = new ESNote();
			esNote.setNoteId(input.getNoteId());
			esNote.setTitle(input.getTitle());
			esNote.setContent(input.getContent());
			esNote.setProductName(input.getProductName());
			esNote.setUserId(input.getUserId());
			return esNote;
		}
	};
}
