package com.duoshouji.server.rest.note;

import java.math.BigDecimal;
import java.net.URL;

public class NoteJson {

	private long noteId;
	private String title;
	private URL image;
	private URL portrait;
	private int rank;
	private int likeCount;
	private int commentCount;
	private BigDecimal listPrice;
	private BigDecimal discountPrice;

	public long getNoteId() {
		return noteId;
	}

	public String getTitle() {
		return title;
	}

	public URL getImage() {
		return image;
	}

	public URL getPortrait() {
		return portrait;
	}

	public int getRank() {
		return rank;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public BigDecimal getListPrice() {
		return listPrice;
	}

	public BigDecimal getDiscountPrice() {
		return discountPrice;
	}

	void setNoteId(long noteId) {
		this.noteId = noteId;
	}

	void setTitle(String title) {
		this.title = title;
	}

	void setImage(URL image) {
		this.image = image;
	}

	void setPortrait(URL portrait) {
		this.portrait = portrait;
	}

	void setRank(int rank) {
		this.rank = rank;
	}

	void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	void setListPrice(BigDecimal listPrice) {
		this.listPrice = listPrice;
	}

	void setDiscountPrice(BigDecimal discountPrice) {
		this.discountPrice = discountPrice;
	}

}
