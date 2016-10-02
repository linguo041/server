package com.duoshouji.server.internal.core;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
import com.duoshouji.server.service.interaction.BasicNoteAndOwner;
import com.duoshouji.server.service.interaction.NoteCommentAndAuthor;
import com.duoshouji.server.service.interaction.NoteDetailAndOwner;
import com.duoshouji.server.service.interaction.UserNoteInteraction;
import com.duoshouji.server.service.note.BasicNote;
import com.duoshouji.server.service.note.CommentPublishAttributes;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NoteComment;
import com.duoshouji.server.service.note.NoteFilter;
import com.duoshouji.server.service.note.NotePublishAttributes;
import com.duoshouji.server.service.note.NotePublishException;
import com.duoshouji.server.service.note.NoteRepository;
import com.duoshouji.server.service.note.recommand.EcommerceItem;
import com.duoshouji.server.service.note.recommand.NoteRecommendService;
import com.duoshouji.server.service.user.BasicUser;
import com.duoshouji.server.service.user.BasicUserAttributes;
import com.duoshouji.server.service.user.FullFunctionalUser;
import com.duoshouji.server.service.user.UserProfile;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.server.service.verify.SecureAccessFacade;
import com.duoshouji.server.util.Location;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;
import com.duoshouji.util.MobileNumber;

@Service
public class DuoShouJiFacadeImpl implements DuoShouJiFacade {
	
	private NoteRepository noteRepository;
	private UserRepository userRepository;
	private UserNoteInteraction interactionFacade;
	private NoteRecommendService noteRecommendService;
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
	public void setNoteRecommendService(NoteRecommendService noteRecommendService) {
		this.noteRecommendService = noteRecommendService;
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
	public void sendLoginVerificationCode(MobileNumber accountId) {
		secureAccessFacade.getSecureChecker(accountId).sendVerificationCode();
	}

	@Override
	public boolean verificationCodeLogin(MobileNumber accountId, VerificationCode verificationCode) {
		return secureAccessFacade.getSecureChecker(accountId).verify(verificationCode);
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
		if (secureAccessFacade.getSecureChecker(accountId).verify(verificationCode)) {
			user.setPassword(password);
			isSuccess = true;
		}
		return isSuccess;
	}

	@Override
	public void sendResetPasswordVerificationCode(MobileNumber accountId) {
		secureAccessFacade.getSecureChecker(accountId).sendVerificationCode();
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
	public void buildFollowConnection(MobileNumber followerId, MobileNumber followedId) {
		final FullFunctionalUser follower = userRepository.findUser(followerId);
		follower.follow(followedId);
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
	public SquareNoteRequester newSquareNoteRequester() {
		return new InnerSquareNoteRequester();
	}
	
	@Override
	public NoteDetailAndOwner getNote(long noteId) {
		Note note = noteRepository.getNote(noteId);
		final BasicUser owner = interactionFacade.getOwner(note);
		return new NoteDetailAndOwner(note, owner);
	}

	@Override
	public CommentPublisher newCommentPublisher(long noteId, MobileNumber userId) {
		return new InnerCommentPublisher(noteId, userId);
	}
	
	@Override
	public List<NoteCommentAndAuthor> getNoteComments(long noteId) {
		List<NoteCommentAndAuthor> results = new LinkedList<NoteCommentAndAuthor>();
		for (NoteComment comment : noteRepository.getNoteComments(noteId)) {
			results.add(new NoteCommentAndAuthor(comment, interactionFacade.getAuthor(comment)));
		}
		return results;
	}

	@Override
	public void likeNote(long noteId, MobileNumber userId) {
		interactionFacade.likeNote(noteId, userId);
	}

	@Override
	public List<EcommerceItem> getNoteRecommendations(long noteId) {
		return noteRecommendService.recommendEcommerceItems(noteRepository.getNote(noteId));
	}

	@Override
	public void inviteFriends(MobileNumber userId, MobileNumber[] mobileNumbers) {
		userRepository.findUser(userId).invitePeopleFromAddressBook(mobileNumbers);
	}

	private class InnerSquareNoteRequester extends NoteFilter implements SquareNoteRequester {
		private Tag tag;
		private Location location;
		private MobileNumber userId;
		
		private InnerSquareNoteRequester() {
			super();
		}
		
		@Override
		public void setTagId(long tagId) {
			tag = tagRepository.findTag(tagId);
		}

		@Override
		public void setWatchedOnly(MobileNumber userId) {
			this.userId = userId;
		}

		@Override
		public void setUserLocation(BigDecimal longitude, BigDecimal latitude) {
			location = new Location(longitude, latitude);
		}

		@Override
		public List<BasicNoteAndOwner> getSquareNotes(long timestamp, int loadedSize, int pageSize) {
			List<BasicNoteAndOwner> result = new LinkedList<BasicNoteAndOwner>();
			for (BasicNote note : listNotes(timestamp).subCollection(loadedSize, loadedSize + pageSize)) {
				BasicUser owner = interactionFacade.getOwner(note);
				result.add(new BasicNoteAndOwner(note, owner));
			}
			return result;
		}
		
		private NoteCollection listNotes(long timestamp) {
			if (userId != null) {
				return interactionFacade.listSquareNotes(this, timestamp, userId);
			} else {
				return noteRepository.listSquareNotes(this, timestamp);
			}
		}
		
		@Override
		public Tag getTag() {
			return tag;
		}
		
		@Override
		public boolean isTagSet() {
			return tag != null;
		}
		
		@Override
		public Location getUserLocation() {
			return location;
		}

		@Override
		public boolean isUserLocationSet() {
			return location != null;
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
	
	private class InnerCommentPublisher implements CommentPublisher, CommentPublishAttributes {
		private long noteId;
		private MobileNumber userId;
		private String comment;
		private Location location;
		private int rating;
		
		public InnerCommentPublisher(long noteId, MobileNumber userId) {
			this.noteId = noteId;
			this.userId = userId;
		}

		@Override
		public void setComment(String comment) {
			this.comment = comment;
		}

		@Override
		public void setRating(int rating) {
			this.rating = rating;
		}
		
		@Override
		public void setLocation(BigDecimal longitude, BigDecimal latitude) {
			location = new Location(longitude, latitude);
		}

		@Override
		public String getComment() {
			return comment;
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
		public void publishComment() {
			interactionFacade.publishComment(noteId, this, userId);
		}
	}
}

