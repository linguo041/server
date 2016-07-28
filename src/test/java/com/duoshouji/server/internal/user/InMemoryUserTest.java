package com.duoshouji.server.internal.user;

import org.junit.Assert;
import org.junit.Test;

import com.duoshouji.server.MockConstants;

public class InMemoryUserTest {

	@Test
	public void testPassword() {
		OperationDelegatingMobileUser user = new OperationDelegatingMobileUser(MockConstants.MOCK_USER_IDENTIFIER);
		
		Assert.assertFalse(user.hasPassword());
		user.setPassword(MockConstants.MOCK_PASSWORD);
		Assert.assertTrue(user.hasPassword());
		Assert.assertTrue(user.verifyPassword(MockConstants.MOCK_PASSWORD));
	}
	
	@Test
	public void testLoginExecutor() {
		OperationDelegatingMobileUser user = new OperationDelegatingMobileUser(MockConstants.MOCK_USER_IDENTIFIER);
		
	}
}
