package com.duoshouji.core.note;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import com.duoshouji.core.Note;
import com.duoshouji.core.NoteFilter;
import com.duoshouji.service.common.Brand;
import com.duoshouji.service.common.Category;
import com.duoshouji.service.common.CommodityCatelogRepository;
import com.duoshouji.service.common.District;
import com.duoshouji.service.common.DistrictRepository;
import com.duoshouji.service.common.Tag;
import com.duoshouji.service.common.TagRepository;
import com.duoshouji.service.note.CommentPublishAttributes;
import com.duoshouji.service.note.NoteCollection;
import com.duoshouji.service.note.NoteComment;
import com.duoshouji.service.note.NoteDetail;
import com.duoshouji.service.note.NoteFacade;
import com.duoshouji.service.note.NotePublishAttributes;
import com.duoshouji.service.note.NotePublishException;
import com.duoshouji.service.user.UserFacade;
import com.duoshouji.service.util.Image;
import com.duoshouji.service.util.Location;

@Service
public class NoteFacadeImpl implements NoteFacade {

	private NoteRepository noteRepository;
	private TagRepository tagRepository;
	private CommodityCatelogRepository commodityCatelogRepository;
	private DistrictRepository districtRepository;

	@Required
	@Autowired
	public void setNoteRepository(NoteRepository noteRepository) {
		this.noteRepository = noteRepository;
	}

	@Required
	@Autowired
	public void setTagRepository(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
	}

	@Required
	@Autowired
	public void setCommodityCatelogRepository(
			CommodityCatelogRepository commodityCatelogRepository) {
		this.commodityCatelogRepository = commodityCatelogRepository;
	}

	@Required
	@Autowired
	public void setDistrictRepository(DistrictRepository districtRepository) {
		this.districtRepository = districtRepository;
	}

	@Override
	public SquareNoteRequester newSquareNoteRequester() {
		return new InnerSquareNoteRequester();
	}

	@Override
	public NoteCollection listPublishedNotes(long authorId, long timestamp) {
		return noteRepository.listPublishedNotes(authorId, timestamp);
	}

	@Override
	public NoteDetail getNote(long noteId) {
		return noteRepository.getNote(noteId);
	}

	@Override
	public List<NoteComment> getNoteComments(long noteId) {
		return noteRepository.getNote(noteId).getComments();
	}

	@Override
	public NotePublisher newNotePublisher(long userId) {
		return new InnerNoteBuilder(userId);
	}

	@Override
	public void publishComment(long userId, long noteId, CommentPublishAttributes commentAttributes) {
		noteRepository.getNote(noteId).addComment(userId, commentAttributes);
	}

	@Override
	public void likeNote(long userId, long noteId) {
		noteRepository.getNote(noteId).likedByUser(userId);
	}

	@Override
	public void setNoteImages(long noteId, Image[] images) {
		noteRepository.getNote(noteId).setImages(images);
	}
	
	
	private class InnerSquareNoteRequester implements SquareNoteRequester, NoteFilter {
		private Tag tag;
		private Location location;
		private long followerId = UserFacade.NULL_USER_ID;
		
		private InnerSquareNoteRequester() {
			super();
		}
		
		@Override
		public void setTagId(long tagId) {
			tag = tagRepository.findTag(tagId);
		}

		@Override
		public void setFollowedNoteOnly(long followerId) {
			this.followerId = followerId;
		}

		@Override
		public void setLocation(BigDecimal longitude, BigDecimal latitude) {
			location = new Location(longitude, latitude);
		}

		@Override
		public NoteCollection getSquareNotes(long timestamp) {
			return noteRepository.listSquareNotes(this, followerId, timestamp);
		}
		
		@Override
		public Tag getChannel() {
			return tag;
		}
		
		@Override
		public boolean isChannelSet() {
			return tag != null;
		}
		
		@Override
		public Location getLocation() {
			return location;
		}

		@Override
		public boolean isLocationSet() {
			return location != null;
		}
	}
	
	private class InnerNoteBuilder implements NotePublisher, NotePublishAttributes {
		
		private final long userId;
		
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
		
		private InnerNoteBuilder(long userId) {
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
			final Note note = noteRepository.createNote(userId, this);
			note.getAuthor().firePublishNote();
			return note.getNoteId();
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
