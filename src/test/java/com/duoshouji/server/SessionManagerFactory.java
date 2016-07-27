package com.duoshouji.server;

import org.glassfish.hk2.api.Factory;

import com.duoshouji.server.session.SessionManager;

public class SessionManagerFactory implements Factory<SessionManager> {

	@Override
	public SessionManager provide() {
		return MockSessionManager.getInstance();
	}

	@Override
	public void dispose(SessionManager instance) {		
	}

}
