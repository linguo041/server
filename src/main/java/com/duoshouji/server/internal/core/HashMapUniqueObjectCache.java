package com.duoshouji.server.internal.core;

import java.util.HashMap;

import com.duoshouji.server.annotation.Unique;

public class HashMapUniqueObjectCache implements UniqueObjectCache {

	private HashMap<Key, Object> objectCache = new HashMap<Key, Object>();
	
	@Override
	public void put(Object identifier, Object object) {
		final Key key = new Key(identifier, getUniqueType(object.getClass()));
		objectCache.put(key, object);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Object identifier, Class<T> objectType) {
		final Key key = new Key(identifier, getUniqueType(objectType));
		return (T) objectCache.get(key);
	}

	private Class<?> getUniqueType(Class<?> objectType) {
		Class<?> uniqueType = findUniqueType(objectType);
		if (uniqueType == null) {
			throw new IllegalArgumentException("Object type "+objectType.getClass().getName()+" is not a unique type.");
		}
		return uniqueType;
	}
	
	private Class<?> findUniqueType(Class<?> objectType) {
		if (objectType.isAnnotationPresent(Unique.class)) {
			return objectType;
		}
		Class<?> resultClass = null;
		for (Class<?> clazz : objectType.getInterfaces()) {
			resultClass = findUniqueType(clazz);
			if (resultClass != null) {
				break;
			}
		}
		if (resultClass == null) {
			if (objectType.getSuperclass() != null) {
				resultClass = findUniqueType(objectType.getSuperclass());
			}
		}
		return resultClass;
	}
	
	private static class Key {
		Object identifier;
		Class<?> objectType;
		
		private Key(Object identifier, Class<?> objectType) {
			super();
			this.identifier = identifier;
			this.objectType = objectType;
		}

		@Override
		public int hashCode() {
			return identifier.hashCode() ^ objectType.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			Key that = (Key) obj;
			return identifier.equals(that.identifier) && objectType.equals(that.objectType);
		}
	}
}
