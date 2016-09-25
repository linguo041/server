package com.duoshouji.server.internal.core;

interface FollowAware {

	void fireBeingFollowed();
	
	void fireActivateFollow();
}
