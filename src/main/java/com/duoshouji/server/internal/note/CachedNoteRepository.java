package com.duoshouji.server.internal.note;

import java.util.HashMap;

import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NoteRepository;

public class CachedNoteRepository implements NoteRepository {

	private NoteRepository delegator;
	private HashMap<CacheKey, NoteCollection> cache;
	
	public CachedNoteRepository(NoteRepository delegator) {
		super();
		this.delegator = delegator;
		cache = new HashMap<CacheKey, NoteCollection>();
	}
	
	@Override
	public NoteCollection findNotes() {
		NoteCollection returnValue = cache.get(CacheKey.NULL_CACHE_KEY);
		if (returnValue == null) {
			returnValue = delegator.findNotes();
			cache.put(CacheKey.NULL_CACHE_KEY, returnValue);
		}
		return returnValue;
	}
	
	@Override
	public NoteCollection findNotes(int tagId) {
		final CacheKey cacheKey = new CacheKey(tagId);
		NoteCollection returnValue = cache.get(cacheKey);
		if (returnValue == null) {
			returnValue = delegator.findNotes(tagId);
			cache.put(cacheKey, returnValue);
		}
		return returnValue;
	}
	
	private static class CacheKey {
		
		static final CacheKey NULL_CACHE_KEY = new CacheKey(-1);
		
		int tagId;

		public CacheKey(int tagId) {
			super();
			this.tagId = tagId;
		}

		@Override
		public int hashCode() {
			return Integer.hashCode(tagId);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof CacheKey))
				return false;
			CacheKey other = (CacheKey) obj;
			return tagId == other.tagId;
		}
	}
}
