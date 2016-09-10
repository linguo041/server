package com.duoshouji.server.amqp.produce;

import lombok.Getter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Created by roy.guo on 7/21/16.
 */
public class Producer<T> {

    @Getter
    private RabbitTemplate rabbitTemplate;

    public Producer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(T obj) {
        rabbitTemplate.convertAndSend(obj);
    }
}
