package com.duoshouji.server.internal.core;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import com.duoshouji.server.service.DuoShouJiFacade;
import com.duoshouji.server.service.interaction.UserNoteInteraction;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NoteFilter;
import com.duoshouji.server.service.note.NotePublishAttributes;
import com.duoshouji.server.service.note.NoteRepository;
import com.duoshouji.server.service.note.PushedNote;
import com.duoshouji.server.service.note.Tag;
import com.duoshouji.server.service.note.TagRepository;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.server.service.verify.SecureAccessFacade;
import com.duoshouji.server.service.verify.SecureChecker;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

@Service
public class DuoShouJiFacadeImpl implements DuoShouJiFacade {
	
	private NoteRepository noteRepository;
	private UserRepository userRepository;
	private UserNoteInteraction interactionFacade;
	private TagRepository tagRepository;
	private SecureAccessFacade secureAccessFacade;
	private CollectionCache collectionCache;
	
	@Autowired
	@Required
	public void setNoteRepository(NoteRepository noteRepository) {
		this.noteRepository = noteRepository;
	}

	@Autowired
	@Required
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Autowired
	@Required
	public void setInteractionFacade(UserNoteInteraction interactionFacade) {
		this.interactionFacade = interactionFacade;
	}

	@Autowired
	@Required
	public void setTagRepository(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
	}
	
	@Autowired
	@Required
	public void setSecureAccessFacade(SecureAccessFacade secureAccessFacade) {
		this.secureAccessFacade = secureAccessFacade;
	}
	
	@Autowired
	@Required
	public void setCollectionCache(CollectionCache collectionCache) {
		this.collectionCache = collectionCache;
	}
	
	@Override
	public void sendLoginVerificationCode(MobileNumber mobileNumber) {
		RegisteredUser user = userRepository.findUser(mobileNumber);
		secureAccessFacade.getSecureChecker(user).sendVerificationCode();
	}

	@Override
	public boolean verificationCodeLogin(MobileNumber mobileNumber, VerificationCode verificationCode) {
		SecureChecker checker = secureAccessFacade.getSecureChecker(userRepository.findUser(mobileNumber));
		return checker.verify(verificationCode);
	}

	@Override
	public boolean passwordLogin(MobileNumber mobileNumber, Password password) {
		return userRepository.findUser(mobileNumber).verifyPassword(password);
	}
	
	@Override
	public boolean resetPassword(MobileNumber accountId
			, VerificationCode verificationCode, Password password) {
		final RegisteredUser user = userRepository.findUser(accountId);
		boolean isSuccess = false;
		if (secureAccessFacade.getSecureChecker(user).verify(verificationCode)) {
			user.setPassword(password);
			isSuccess = true;
		}
		return isSuccess;
	}

	@Override
	public void sendResetPasswordVerificationCode(MobileNumber accountId) {
		secureAccessFacade.getSecureChecker(userRepository.findUser(accountId)).sendVerificationCode();
	}

	@Override
	public void updateNickname(MobileNumber accountId, String nickname) {
		userRepository.findUser(accountId).setNickname(nickname);		
	}

	@Override
	public NoteBuilder newNotePublisher(MobileNumber accountId) {
		return new InnerNoteBuilder(accountId);
	}

	@Override
	public NoteCollection getUserPublishedNotes(MobileNumber accountId, boolean refresh) {
		return collectionCache
			.getCollectionRequestor(accountId, interactionFacade, refresh)
			.getUserPublishedNotes(userRepository.findUser(accountId));
	}
	
	@Override
	public SquareNoteRequester newSquareNoteRequester(MobileNumber mobileNumber) {
		return new InnerSquareNoteRequester(mobileNumber);
	}

	@Override
	public List<Tag> getTags() {
		return tagRepository.listTags();
	}
	
	private class InnerSquareNoteRequester implements SquareNoteRequester {
		private final MobileNumber mobileNumber;
		private NoteFilter noteFilter;
		
		private InnerSquareNoteRequester(MobileNumber mobileNumber) {
			super();
			this.mobileNumber = mobileNumber;
			noteFilter = new NoteFilter();
		}

		@Override
		public void setTagId(long tagId) {
			noteFilter.setTag(tagRepository.findTag(tagId));
		}

		@Override
		public NoteCollection pushSquareNotes(boolean refresh) {
			NoteRepository requestor = collectionCache.getCollectionRequestor(mobileNumber, noteRepository, refresh);
			return new PushedNoteCollection(requestor.listNotes(noteFilter));
		}
	}
	
	private class PushedNoteCollection implements NoteCollection {
		
		private NoteCollection delegator;
		
		private PushedNoteCollection(NoteCollection delegator) {
			this.delegator = delegator;
		}

		@Override
		public Iterator<Note> iterator() {
			return new Iterator<Note>() {
				final Iterator<Note> ite = delegator.iterator();
				@Override
				public boolean hasNext() {
					return ite.hasNext();
				}

				@Override
				public Note next() {
					if (!hasNext()) {
						throw new NoSuchElementException();
					}
					final Note note = ite.next();
					return new PushedNote(note, interactionFacade.getOwner(note));
				}
			};
		}

		@Override
		public NoteCollection subCollection(int startIndex, int endIndex) {
			return new PushedNoteCollection(delegator.subCollection(startIndex, endIndex));
		}
	}
	
	private class InnerNoteBuilder implements NoteBuilder {
		
		private MobileNumber userId;
		private long noteId = -1;
		private NotePublishAttributes valueHolder = new NotePublishAttributes();
		
		private InnerNoteBuilder(MobileNumber userId) {
			this.userId = userId;
		}

		@Override
		public void setTitle(String title) {
			valueHolder.setTitle(title);
		}

		@Override
		public void setContent(String content) {
			valueHolder.setContent(content);
		}

		@Override
		public void setTags(long[] tags) {
			for (long tagId : tags) {
				valueHolder.addTag(tagRepository.findTag(tagId));
			}
		}
		
		@Override
		public long publishNote() {
			if (noteId < 0) {
				noteId = interactionFacade.publishNote(userRepository.findUser(userId), valueHolder);
			}
			return noteId;
		}
	}
}
