package com.duoshouji.server.amqp.produce;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.duoshouji.server.amqp.config.MessageQueueManager;
import com.duoshouji.server.amqp.config.QueueConfig;

/**
 * Created by roy.guo on 7/21/16.
 */
public class ProducerFactory extends MessageQueueManager{

    public Producer create (QueueConfig queueConfig) {
        super.declare(queueConfig);

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setChannelTransacted(queueConfig.isChannelTrasacted());
//		rabbitTemplate.setRetryTemplate();
//		rabbitTemplate.setMessageConverter();
		rabbitTemplate.setExchange(queueConfig.getExchangeName());

        return new Producer(rabbitTemplate);
    }
}
