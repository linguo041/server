package com.duoshouji.server.user;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface RegisteredUser {

	AccountSecurity getAccountSecurity();
}
