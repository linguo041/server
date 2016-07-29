package com.duoshouji.server.service.user;

import org.glassfish.jersey.spi.Contract;

import com.duoshouji.server.util.MobileNumber;

@Contract
public interface UserFacade {

	RegisteredUser getUser(MobileNumber mobileNumber);
}
