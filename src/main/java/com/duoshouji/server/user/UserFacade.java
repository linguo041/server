package com.duoshouji.server.user;

import org.glassfish.jersey.spi.Contract;

@Contract
public interface UserFacade {

	void register(String accountId, String password);
}
