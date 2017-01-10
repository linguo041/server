package com.duoshouji.core;

import com.duoshouji.service.util.MobileNumber;


public interface MessageProxyFactory {

	MessageProxy getMessageProxy(MobileNumber mobileNumber);
}
