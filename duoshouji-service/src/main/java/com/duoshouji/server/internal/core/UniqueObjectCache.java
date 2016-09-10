package com.duoshouji.server.internal.core;

public interface UniqueObjectCache {

	void put(Object identifier, Object object);
	
	<T> T get(Object identifier, Class<T> objectType);
}
