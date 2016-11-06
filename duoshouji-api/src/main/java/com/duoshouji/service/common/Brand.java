package com.duoshouji.service.common;

public final class Brand {
	private final long id;
	private final String name;
	
	Brand(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

}
