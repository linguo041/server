package com.duoshouji.server;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.duoshouji.server.internal.dao.CommentDtoCollection;
import com.duoshouji.server.internal.dao.InMemoryNoteAlbumDto;
import com.duoshouji.server.internal.dao.InMemoryNoteDto;
import com.duoshouji.server.internal.dao.InMemoryRegisteredUserDto;
import com.duoshouji.server.internal.dao.LikeDtoCollection;
import com.duoshouji.server.internal.dao.NoteAlbumDto;
import com.duoshouji.server.internal.dao.NoteDto;
import com.duoshouji.server.internal.dao.NoteDtoCollection;
import com.duoshouji.server.internal.dao.RegisteredUserDto;
import com.duoshouji.server.internal.dao.UserNoteDao;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.MobileNumber;

@Service
public class MockMysqlDao implements UserNoteDao {

	private List<InMemoryRegisteredUserDto> userDtos;
	private List<NoteDto> noteDtos;
	
	public MockMysqlDao() {
		userDtos = new LinkedList<InMemoryRegisteredUserDto>();
		noteDtos = new LinkedList<NoteDto>();
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
	public NoteDtoCollection findNotes() {
		return new InMemoryNoteDtoCollection();
	}

	@Override
	public NoteAlbumDto findNoteAlbum(NoteDto noteDto) {
		return ((InnerNoteDto)noteDto).noteAlbumDto;
	}

	@Override
	public RegisteredUserDto getOwner(NoteDto noteDto) {
		return ((InnerNoteDto)noteDto).userDto;
	}

	@Override
	public LikeDtoCollection getLikes(NoteDto noteDto) {
		return new LikeDtoCollection() {
			@Override
			public int size() {
				return 0;
			}
		};
	}

	@Override
	public CommentDtoCollection getComments(NoteDto noteDto) {
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
		public InnerNoteDto(long noteId, String title, int rank) {
			super(noteId, title, rank);
		}
	}
	
	private class InMemoryNoteDtoCollection implements NoteDtoCollection {
		
		@Override
		public Iterator<NoteDto> iterator() {
			return noteDtos.iterator();
		}
		
	}
}
