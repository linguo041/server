package com.duoshouji.server.internal.core;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import com.duoshouji.server.service.DuoShouJiFacade;
import com.duoshouji.server.service.common.Brand;
import com.duoshouji.server.service.common.Category;
import com.duoshouji.server.service.common.CommodityCatelogRepository;
import com.duoshouji.server.service.common.District;
import com.duoshouji.server.service.common.DistrictRepository;
import com.duoshouji.server.service.common.Tag;
import com.duoshouji.server.service.common.TagRepository;
import com.duoshouji.server.service.interaction.UserNoteInteraction;
import com.duoshouji.server.service.note.BasicNote;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NoteFilter;
import com.duoshouji.server.service.note.NotePublishAttributes;
import com.duoshouji.server.service.note.NotePublishException;
import com.duoshouji.server.service.note.NoteRepository;
import com.duoshouji.server.service.note.PushedNote;
import com.duoshouji.server.service.user.BasicUserAttributes;
import com.duoshouji.server.service.user.FullFunctionalUser;
import com.duoshouji.server.service.user.UserProfile;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.server.service.verify.SecureAccessFacade;
import com.duoshouji.server.service.verify.SecureChecker;
import com.duoshouji.server.util.Location;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

@Service
public class DuoShouJiFacadeImpl implements DuoShouJiFacade {
	
	private NoteRepository noteRepository;
	private UserRepository userRepository;
	private UserNoteInteraction interactionFacade;
	private TagRepository tagRepository;
	private CommodityCatelogRepository commodityCatelogRepository;
	private DistrictRepository districtRepository;
	private SecureAccessFacade secureAccessFacade;
	private CollectionCache collectionCache = new CollectionCache();
	
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
	public void setCommodityCatelogRepository(CommodityCatelogRepository commodityCatelogRepository) {
		this.commodityCatelogRepository = commodityCatelogRepository;
	}

	@Autowired
	@Required
	public void setDistrictRepository(DistrictRepository districtRepository) {
		this.districtRepository = districtRepository;
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
	
	@Override
	public void sendLoginVerificationCode(MobileNumber mobileNumber) {
		FullFunctionalUser user = userRepository.findUser(mobileNumber);
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
	public UserProfile getUserProfile(MobileNumber mobileNumber) {
		return userRepository.findUser(mobileNumber);
	}

	@Override
	public boolean resetPassword(MobileNumber accountId
			, VerificationCode verificationCode, Password password) {
		final FullFunctionalUser user = userRepository.findUser(accountId);
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
	public void updateProfile(MobileNumber accountId, BasicUserAttributes attributes) {
		final FullFunctionalUser user = userRepository.findUser(accountId);
		if (attributes.getNickname() != null) {
			user.setNickname(attributes.getNickname());
		}
		if (attributes.getGender() != null) {
			user.setGender(attributes.getGender());
		}	
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
		return tagRepository.listChannels();
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
		public Iterator<BasicNote> iterator() {
			return new Iterator<BasicNote>() {
				final Iterator<BasicNote> ite = delegator.iterator();
				@Override
				public boolean hasNext() {
					return ite.hasNext();
				}

				@Override
				public BasicNote next() {
					if (!hasNext()) {
						throw new NoSuchElementException();
					}
					final BasicNote note = ite.next();
					return new PushedNote(note, interactionFacade.getOwner(note));
				}
			};
		}

		@Override
		public NoteCollection subCollection(int startIndex, int endIndex) {
			return new PushedNoteCollection(delegator.subCollection(startIndex, endIndex));
		}
	}
	
	private class InnerNoteBuilder implements NoteBuilder, NotePublishAttributes {
		
		private final MobileNumber userId;
		private long noteId = -1;
		
		private Category category;
		private Brand brand;
		private String productName;
		private District district;
		private BigDecimal price;
		
		private String title;
		private String content;
		private List<Tag> tags = Collections.emptyList();
		private int rating;
		private Location location;
		
		private InnerNoteBuilder(MobileNumber userId) {
			this.userId = userId;
		}

		@Override
		public void setTitle(String title) {
			this.title = title;
		}

		@Override
		public void setContent(String content) {
			this.content = content;
		}

		@Override
		public void setTags(long[] tagIds) {
			if (tagIds.length >= MAX_TAG_COUNT) {
				throw new NotePublishException("Tag count has exceeds maximum value; maximum allowed tag count: 9");
			}
			Tag[] tags = new Tag[tagIds.length];
			for (int i = 0; i < tags.length; ++i) {
				tags[i] = tagRepository.findTag(tagIds[i]);
			}
			this.tags = Arrays.asList(tags);
		}
		
		@Override
		public void setCategoryId(long categoryId) {
			category = commodityCatelogRepository.getCategory(categoryId);
			
		}

		@Override
		public void setBrandId(long brandId) {
			brand = commodityCatelogRepository.getBrand(brandId);
		}

		@Override
		public void setProductName(String productName) {
			this.productName = productName;
		}

		@Override
		public void setPrice(BigDecimal price) {
			this.price = price;
		}

		@Override
		public void setDistrictId(long districtId) {
			district = districtRepository.getDistrict(districtId);
			
		}

		@Override
		public void setRating(int rating) {
			this.rating = rating;
		}

		@Override
		public void setLocation(BigDecimal longitude, BigDecimal latitude) {
			this.location = new Location(longitude, latitude);
		}
		
		@Override
		public long publishNote() {
			if (noteId < 0) {
				noteId = interactionFacade.publishNote(userRepository.findUser(userId), this);
			}
			return noteId;
		}

		@Override
		public Category getCategory() {
			return category;
		}

		@Override
		public Brand getBrand() {
			return brand;
		}

		@Override
		public String getProductName() {
			return productName;
		}

		@Override
		public District getDistrict() {
			return district;
		}

		@Override
		public BigDecimal getPrice() {
			return price;
		}

		@Override
		public int getRating() {
			return rating;
		}

		@Override
		public Location getLocation() {
			return location;
		}

		@Override
		public String getTitle() {
			return title;
		}

		@Override
		public String getContent() {
			return content;
		}

		@Override
		public List<Tag> getTags() {
			return tags;
		}

		@Override
		public int getTagCount() {
			return tags.size();
		}
	}
}

