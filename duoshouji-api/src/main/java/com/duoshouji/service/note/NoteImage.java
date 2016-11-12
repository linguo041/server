package com.duoshouji.service.note;

import com.duoshouji.service.util.Image;

public class NoteImage extends Image {

	private String marks;
	
	public NoteImage(int width, int height, String url, String marks) {
		super(width, height, url);
		this.marks = marks;
	}

	public String getMarks() {
		return marks;
	}
}
