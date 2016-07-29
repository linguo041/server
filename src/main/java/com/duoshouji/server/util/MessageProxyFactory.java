package com.duoshouji.server.util;

import com.duoshouji.server.service.user.RegisteredUser;

public interface MessageProxyFactory {

	UserMessageProxy getMessageProxy(RegisteredUser user);
}
