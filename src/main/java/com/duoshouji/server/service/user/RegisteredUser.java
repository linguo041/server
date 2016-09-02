package com.duoshouji.server.service.user;

import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.UserMessageProxy;

public interface RegisteredUser extends BasicUserAttributes {

	UserMessageProxy getMessageProxy();

	boolean verifyPassword(Password password);

	boolean hasPassword();

	void logout();

	void setPassword(Password password);

	void setNickname(String nickname);
}
