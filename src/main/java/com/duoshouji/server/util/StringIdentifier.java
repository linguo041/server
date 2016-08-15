package com.duoshouji.server.util;

public abstract class StringIdentifier implements Identifier {

	private final String identifier;
	
	protected StringIdentifier(String identifier) {
		super();
		if (identifier == null) {
			throw new IllegalArgumentException("Identifier string can't be null!");
		}
		this.identifier = identifier;
	}
	
	@Override
	public String toString() {
		return getValue();
	}
	
	protected String getValue() {
		return identifier;
	}
}
