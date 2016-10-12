package com.duoshouji.server.amqp.consume;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

/**
 * Created by roy.guo on 7/18/16.
 */
public class SimpleConsumer implements MessageListener{

    @Override
    public void onMessage(Message message) {
        System.out.println("message: " + message.toString());
    }
}
