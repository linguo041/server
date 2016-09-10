package com.duoshouji.server.amqp.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import lombok.Getter;
import lombok.Setter;

public class MessageQueueManager {

	@Getter
    @Setter
    protected ConnectionFactory connectionFactory;

    @Getter
    @Setter
    protected RabbitAdmin rabbitAdmin;
    
    protected void declare (QueueConfig queueConfig) {
    	// declare exchange
    	FanoutExchange exchange = new FanoutExchange(queueConfig.getExchangeName(), true, false);
    	rabbitAdmin.declareExchange(exchange);

    	// declare queue and bind
    	for (String queueName : queueConfig.getQueueNames()) {
    		Queue queue = new Queue(queueName, true, false, false);
    		rabbitAdmin.declareQueue(queue);
    		
    		Binding binding = BindingBuilder.bind(queue).to(exchange);
			rabbitAdmin.declareBinding(binding);
    	}
    }
}
