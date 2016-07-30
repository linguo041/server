package com.duoshouji.server.amqp.consume;

import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import com.duoshouji.server.amqp.config.QueueConfig;

import lombok.Getter;

public class ConsumerContainerFactory {
	
	@Getter
	private ConnectionFactory connectionFactory;
	
	@Getter
	private RabbitAdmin rabbitAdmin;
	
	public ConsumerContainerFactory (ConnectionFactory connectionFactory, RabbitAdmin rabbitAdmin) {
		this.connectionFactory = connectionFactory;
		this.rabbitAdmin = rabbitAdmin;
	}

	public ConsumerContainer createConsumerContainer(QueueConfig param, MessageListener consumer) {
		declare(param);
		
		ConsumerContainer container = new ConsumerContainer(connectionFactory);
		container.setQueueNames(param.getQueueName());
		container.setChannelTransacted(param.isChannelTrasacted());
		container.setConcurrentConsumers(param.getConsumerNum());
		container.setDefaultRequeueRejected(param.isRequestRejected());
		container.setMessageListener(consumer);
		container.setAutoStartup(param.isAutoStart());
		return container;
	}
	
	private void declare (QueueConfig param) {
		
	}
}
