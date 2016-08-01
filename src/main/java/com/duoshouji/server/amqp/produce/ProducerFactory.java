package com.duoshouji.server.amqp.produce;

import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Created by roy.guo on 7/21/16.
 */
public class ProducerFactory {

    @Getter
    @Setter
    private ConnectionFactory connectionFactory;

    @Getter
    @Setter
    private RabbitAdmin rabbitAdmin;

    public Producer create (/* queue parameters */) {
        // declare exchange
        // not need to declare queue and binding in producer but consumer
        // declare retry queue

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange("");
        // set other config

        return new Producer(rabbitTemplate);
    }
}
