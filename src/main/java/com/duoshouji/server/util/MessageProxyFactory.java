package com.duoshouji.server.util;

import com.duoshouji.server.user.RegisteredUser;

public interface MessageProxyFactory {

	UserMessageProxy getMessageProxy(RegisteredUser user);
}
