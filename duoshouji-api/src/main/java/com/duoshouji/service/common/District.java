package com.duoshouji.service.common;


public final class District {
	
	private final long id;
	private final String name;
	
	District(long id, String name) {
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
