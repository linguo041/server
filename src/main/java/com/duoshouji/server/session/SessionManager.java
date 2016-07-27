package com.duoshouji.server.session;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface SessionManager {

	String newToken(String accountId);

}
