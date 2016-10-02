package com.duoshouji.server.internal.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.duoshouji.server.annotation.Collection;

@Service
public class CollectionCache {
	
	private static HashSet<Class<?>> compiledClasses = new HashSet<Class<?>>();
	private static HashSet<Method> proxiedMethods = new HashSet<Method>();
	
	private HashMap<Object, List<InvocationResultHolder>> cache;
	
	private static void compile(Class<?> requestorClass) {
		if (compiledClasses.contains(requestorClass)) {
			return;
		}
		for (Class<?> implementedIntefaces : requestorClass.getInterfaces()) {
			compile(implementedIntefaces);
		}
		for (Method method : requestorClass.getMethods()) {
			if (method.getReturnType().isAnnotationPresent(Collection.class)) {
				proxiedMethods.add(method);
			}
		}
		compiledClasses.add(requestorClass);
	}
	
	public CollectionCache() {
		cache = new HashMap<Object, List<InvocationResultHolder>>();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getCollectionRequestor(Object accessKey, T requestor, boolean refresh) {
		compile(requestor.getClass());
		List<InvocationResultHolder> handlers = cache.get(accessKey);
		if (handlers == null) {
			handlers = new LinkedList<InvocationResultHolder>();
			cache.put(accessKey, handlers);
		}
		InvocationResultHolder targetHandler = null;
		for (InvocationResultHolder handler : handlers) {
			if (handler.delegator == requestor) {
				targetHandler = handler;
				break;
			}
		}
		if (targetHandler == null) {
			targetHandler = new InvocationResultHolder(requestor);
			handlers.add(targetHandler);
		}
		targetHandler.refresh = refresh;
		return (T) Proxy.newProxyInstance(
				getClass().getClassLoader()
				, requestor.getClass().getInterfaces()
				, targetHandler);
	}
	
	private class InvocationResultHolder implements InvocationHandler {
		private final Object delegator;
		private Map<ArgumentListKey, Object> lastResults;
		private boolean refresh;
		
		private InvocationResultHolder(Object delegator) {
			this.delegator = delegator;
			lastResults = new HashMap<ArgumentListKey, Object>();
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (proxiedMethods.contains(method)) {
				ArgumentListKey key = new ArgumentListKey(args);
				Object lastResult = lastResults.get(key);
				if (refresh || lastResult == null) {
					lastResult = method.invoke(delegator, args);
					lastResults.put(key, lastResult);
				}
				return lastResult;
			}
			return method.invoke(delegator, args);
		}
	}
	
	private static class ArgumentListKey {
		private Object[] args;
		private ArgumentListKey(Object[] args) {
			super();
			this.args = args;
		}
		
		@Override
		public int hashCode() {
			return Arrays.hashCode(args);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof ArgumentListKey))
				return false;
			ArgumentListKey that = (ArgumentListKey) obj;
			return Arrays.deepEquals(args, that.args);
		}
	}
}
