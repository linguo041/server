package com.duoshouji.server.internal.core;

import org.springframework.stereotype.Service;

import com.duoshouji.server.util.MobileNumber;

@Service
public class CollectionCache {
	
	public CollectionCache() {
	}
	
	public <T> T getCollectionRequestor(MobileNumber callerId, T requestor, boolean refresh) {
		
	}

}
