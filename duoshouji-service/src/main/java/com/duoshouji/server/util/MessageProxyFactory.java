package com.duoshouji.server.util;

import com.duoshouji.util.MobileNumber;


public interface MessageProxyFactory {

	MessageProxy getMessageProxy(MobileNumber mobileNumber);
}
