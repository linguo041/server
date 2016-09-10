package com.duoshouji.server.util;

import com.duoshouji.server.service.user.FullFunctionalUser;

public interface MessageProxyFactory {

	UserMessageProxy getMessageProxy(FullFunctionalUser user);
}
