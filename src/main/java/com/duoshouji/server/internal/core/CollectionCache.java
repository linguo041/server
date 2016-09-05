package com.duoshouji.server.internal.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.duoshouji.server.annotation.Collection;
import com.duoshouji.server.util.MobileNumber;

@Service
public class CollectionCache {
	
	private static HashSet<Class<?>> compiledClasses = new HashSet<Class<?>>();
	private static HashSet<Method> proxiedMethods = new HashSet<Method>();
	
	private HashMap<MobileNumber, List<InvocationResultHolder>> cache;
	
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
		cache = new HashMap<MobileNumber, List<InvocationResultHolder>>();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getCollectionRequestor(MobileNumber callerId, T requestor, boolean refresh) {
		compile(requestor.getClass());
		List<InvocationResultHolder> handlers = cache.get(callerId);
		if (handlers == null) {
			handlers = new LinkedList<InvocationResultHolder>();
			cache.put(callerId, handlers);
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
		private Object lastResult;
		private boolean refresh;
		
		private InvocationResultHolder(Object delegator) {
			this.delegator = delegator;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (proxiedMethods.contains(method)) {
				if (refresh || lastResult == null) {
					lastResult = method.invoke(delegator, args);
				}
				return lastResult;
			}
			return method.invoke(delegator, args);
		}
	}

}
