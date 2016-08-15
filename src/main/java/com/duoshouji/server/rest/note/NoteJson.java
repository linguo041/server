package com.duoshouji.server.rest.note;

import java.net.URL;

public class NoteJson {

	private long noteId;
	private String title;
	private URL image;
	private int imageWidth;
	private int imageHeight;
	private URL portrait;
	private int rank;
	private int likeCount;
	private int commentCount;

	public long getNoteId() {
		return noteId;
	}

	public String getTitle() {
		return title;
	}

	public URL getImage() {
		return image;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
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

	void setNoteId(long noteId) {
		this.noteId = noteId;
	}

	void setTitle(String title) {
		this.title = title;
	}

	void setImage(URL image) {
		this.image = image;
	}

	void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
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

}
