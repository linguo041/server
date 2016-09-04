package com.duoshouji.server.service.user;

import com.duoshouji.server.annotation.Unique;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.UserMessageProxy;

@Unique
public interface RegisteredUser extends BasicUser {

	UserMessageProxy getMessageProxy();

	boolean verifyPassword(Password password);

	boolean hasPassword();

	void setPassword(Password password);

	void setNickname(String nickname);
}
