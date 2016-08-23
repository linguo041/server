package com.duoshouji.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.duoshouji.server.internal.dao.InMemoryNoteAlbumDto;
import com.duoshouji.server.internal.dao.InMemoryNoteDto;
import com.duoshouji.server.internal.dao.InMemoryRegisteredUserDto;
import com.duoshouji.server.service.dao.CommentDtoCollection;
import com.duoshouji.server.service.dao.LikeDtoCollection;
import com.duoshouji.server.service.dao.NoteAlbumDto;
import com.duoshouji.server.service.dao.NoteDto;
import com.duoshouji.server.service.dao.NoteDtoCollection;
import com.duoshouji.server.service.dao.RegisteredUserDto;
import com.duoshouji.server.service.dao.UserNoteDao;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.MobileNumber;

@Service
public class MockMysqlDao implements UserNoteDao {

	private List<InMemoryRegisteredUserDto> userDtos;
	private List<NoteDto> noteDtos;
	
	public MockMysqlDao() {
		userDtos = new LinkedList<InMemoryRegisteredUserDto>();
		noteDtos = new ArrayList<NoteDto>();
		loadExistingUsers();
	}
	
	private void loadExistingUsers() {
		InMemoryRegisteredUserDto userDto = new InMemoryRegisteredUserDto(
				MockConstants.MOCK_USER_IDENTIFIER
				, MockConstants.MOCK_MOBILE_NUMBER);
		userDto.setPasswordDigest(MockConstants.MOCK_PASSWORD.toString());
		userDto.setPortrait(MockConstants.MOCK_USER_PORTRAIT);
		userDtos.add(userDto);
	}
	
	public void addNoteForMockUser() {
		InnerNoteDto noteDto = new InnerNoteDto(
				noteDtos.size(), MockConstants.MOCK_NOTE_TITLE, 0);
		noteDto.userDto = userDtos.get(0);
		noteDto.noteAlbumDto = new InMemoryNoteAlbumDto(MockConstants.MOCK_NOTE_MAIN_IMAGE);
		noteDtos.add(noteDto);
	}
	
	@Override
	public RegisteredUserDto findUser(UserIdentifier userId) {
		for (InMemoryRegisteredUserDto u : userDtos) {
			if (u.getUserId().equals(userId))
				return u;
		}
		return null;
	}
	

	@Override
	public void addUser(UserIdentifier userId, MobileNumber mobileNumber) {
		userDtos.add(new InMemoryRegisteredUserDto(userId, mobileNumber));
	}

	@Override
	public NoteAlbumDto findNoteAlbum(NoteDto noteDto) {
		return ((InnerNoteDto)noteDto).noteAlbumDto;
	}

	@Override
	public RegisteredUserDto findOwner(NoteDto noteDto) {
		return ((InnerNoteDto)noteDto).userDto;
	}

	@Override
	public LikeDtoCollection findLikes(NoteDto noteDto) {
		return new LikeDtoCollection() {
			@Override
			public int size() {
				return 0;
			}
		};
	}

	@Override
	public CommentDtoCollection findComments(NoteDto noteDto) {
		return new CommentDtoCollection() {
			@Override
			public int size() {
				return 0;
			}
			
		};
	}
	
	private class InnerNoteDto extends InMemoryNoteDto {
		InMemoryRegisteredUserDto userDto;
		InMemoryNoteAlbumDto noteAlbumDto;
		long createdTime;
		public InnerNoteDto(long noteId, String title, int rank) {
			super(noteId, title, rank);
			createdTime = System.currentTimeMillis();
		}
	}
	
	@Override
	public NoteDtoCollection findNotes(long cutoff) {
		return new InMemoryNoteDtoCollection(findNotesSnapshot(cutoff));
	}

	@Override
	public NoteDtoCollection findNotes(long cutoff, int startIndex, int endIndex) {
		List<NoteDto> snapshot = findNotesSnapshot(cutoff);
		if (startIndex >= snapshot.size()) {
			snapshot = Collections.<NoteDto>emptyList();
		} else {
			snapshot = snapshot.subList(startIndex, Math.min(endIndex, snapshot.size()));
		}
		return new InMemoryNoteDtoCollection(snapshot);
	}
	
	private List<NoteDto> findNotesSnapshot(long cutoff) {
		int cutoffIndex = 0;
		while (cutoffIndex < noteDtos.size()) {
			InnerNoteDto tempDto = (InnerNoteDto) noteDtos.get(cutoffIndex);
			if (tempDto.createdTime >= cutoff) {
				break;
			}
			++cutoffIndex;
		}
		return noteDtos.subList(0, cutoffIndex);
	}
	
	private class InMemoryNoteDtoCollection implements NoteDtoCollection {
		Iterable<NoteDto> ite;
		
		public InMemoryNoteDtoCollection(Iterable<NoteDto> ite) {
			super();
			this.ite = ite;
		}

		@Override
		public Iterator<NoteDto> iterator() {
			return ite.iterator();
		}
	}
}
