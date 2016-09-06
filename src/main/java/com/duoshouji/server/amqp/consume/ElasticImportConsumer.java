package com.duoshouji.server.amqp.consume;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

public class ElasticImportConsumer implements MessageListener {

	@Override
	public void onMessage(Message message) {
		System.out.println("message: " + message.toString());
	}

}
