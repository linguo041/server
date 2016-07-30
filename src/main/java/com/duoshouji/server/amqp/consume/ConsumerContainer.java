package com.duoshouji.server.amqp.consume;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

public class ConsumerContainer extends SimpleMessageListenerContainer {
	public ConsumerContainer(ConnectionFactory connectionFactory) {
		super(connectionFactory);
	}
}
