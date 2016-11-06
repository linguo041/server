package com.duoshouji.core.note;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import com.duoshouji.core.FullFunctionalNote;
import com.duoshouji.core.NoteFilter;
import com.duoshouji.service.common.Brand;
import com.duoshouji.service.common.BrandRepository;
import com.duoshouji.service.common.Category;
import com.duoshouji.service.common.CategoryRepository;
import com.duoshouji.service.common.District;
import com.duoshouji.service.common.DistrictRepository;
import com.duoshouji.service.common.Tag;
import com.duoshouji.service.common.TagRepository;
import com.duoshouji.service.note.CommentPublishAttributes;
import com.duoshouji.service.note.Note;
import com.duoshouji.service.note.NoteCollection;
import com.duoshouji.service.note.NoteComment;
import com.duoshouji.service.note.NoteFacade;
import com.duoshouji.service.note.NotePublishAttributes;
import com.duoshouji.service.user.UserFacade;
import com.duoshouji.service.util.Image;

@Service
public class NoteFacadeImpl implements NoteFacade {

	private NoteRepository noteRepository;
	private TagRepository tagRepository;
	private CategoryRepository categoryRepository;
	private BrandRepository brandRepository;
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
	public void setCategoryRepository(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Required
	@Autowired
	public void setBrandRepository(BrandRepository brandRepository) {
		this.brandRepository = brandRepository;
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
	public Note getNote(long noteId) {
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
		private long followerId = UserFacade.NULL_USER_ID;
		
		private InnerSquareNoteRequester() {
			super();
		}
		
		@Override
		public void setChannelId(long tagId) {
			tag = tagRepository.findChannel(tagId);
		}

		@Override
		public void setFollowedNoteOnly(long followerId) {
			this.followerId = followerId;
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
		private int rating;
		
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
		public void setCategoryId(long categoryId) {
			if (categoryId > 0) {
				category = categoryRepository.findCategory(categoryId);
			}
		}

		@Override
		public void setBrandId(long brandId) {
			if (brandId > 0) {
				brand = brandRepository.findBrand(brandId);
			}
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
			if (districtId > 0) {
				district = districtRepository.findDistrict(districtId);
			}
		}

		@Override
		public void setRating(int rating) {
			this.rating = rating;
		}
		
		@Override
		public long publishNote() {
			final FullFunctionalNote note = noteRepository.createNote(userId, this);
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
		public String getTitle() {
			return title;
		}

		@Override
		public String getContent() {
			return content;
		}

		@Override
		public boolean isCategorySet() {
			return category != null;
		}

		@Override
		public boolean isBrandSet() {
			return brand != null;
		}

		@Override
		public boolean isProductNameSet() {
			return productName != null;
		}

		@Override
		public boolean isDistrictSet() {
			return district != null;
		}

		@Override
		public boolean isPriceSet() {
			return price != null;
		}
	}
}
