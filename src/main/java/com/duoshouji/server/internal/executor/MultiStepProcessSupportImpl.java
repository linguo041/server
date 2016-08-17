package com.duoshouji.server.internal.executor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duoshouji.server.service.executor.MultiStepProcessSupport;
import com.duoshouji.server.service.executor.NoSuchExecutorException;
import com.duoshouji.server.service.executor.VerificationCodeLoginExecutor;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.util.MessageProxyFactory;
import com.duoshouji.server.util.VerificationCodeGenerator;

@Service
public class MultiStepProcessSupportImpl implements MultiStepProcessSupport, ExecutorHolder {
	
	private HashMap<RegisteredUser, VerificationCodeLoginExecutor> executors;
	private VerificationCodeGenerator codeGenerator;
	private MessageProxyFactory messageProxyFactory;

	@Autowired
	public MultiStepProcessSupportImpl(VerificationCodeGenerator codeGenerator, MessageProxyFactory messageProxyFactory) {
		super();
		this.executors = new HashMap<RegisteredUser, VerificationCodeLoginExecutor>();
		this.codeGenerator = codeGenerator;
		this.messageProxyFactory = messageProxyFactory;
	}

	@Override
	public VerificationCodeLoginExecutor getVerificationCodeLoginExecutor(RegisteredUser user) {
		VerificationCodeLoginExecutor executor = executors.get(user);
		if (executor == null) {
			executor = new DelegatedVerificationCodeLoginExecutor(
					new SmsVerificationCodeAuthenticationExecutor(messageProxyFactory.getMessageProxy(user), codeGenerator), this);
			executors.put(user, executor);
		}
		return executor;
	}

	@Override
	public void detachExecutor(Object executor) {
		Iterator<Map.Entry<RegisteredUser, VerificationCodeLoginExecutor>> ite = executors.entrySet().iterator();
		while (ite.hasNext()) {
			if (ite.next().getValue() == executor) {
				ite.remove();
				return;
			}
		}
		throw new NoSuchExecutorException();
	}
}
