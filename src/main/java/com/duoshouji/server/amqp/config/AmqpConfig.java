package com.duoshouji.server.amqp.config;

/**
 * Created by roy.guo on 7/21/16.
 */
public final class AmqpConfig {
    public static class RabbitMqConfig {
        public final static String MQ_SERVER_HOST = "localhost";
        public final static String MQ_SERVER_USER = "guest";
        public final static String MQ_SERVER_PASS = "guest";
    }
    
    public class NoteChangeMqConfig {
    	public final static String EXCHANGE_NAME = "duoshouji.exchange.note.change";
    	public final static String QUEUE_NAME = "duoshouji.queue.note.change";
    	public final static int CONSUMER_NUM = 5;
    	public final static boolean CONSUMER_AUTO_START = true;
    	public final static boolean CHANNEL_TRANSACTED = true;
    	public final static boolean REQUEST_REJECTED = false;
    }
    
    public class SimpleConsumerConfig {
    	public final static String EXCHANGE_NAME = "duoshouji.exchange.simple.v1";
    	public final static String QUEUE_NAME = "duoshouji.queue.simple.v1";
    	public final static int CONSUMER_NUM = 5;
    	public final static boolean CONSUMER_AUTO_START = true;
    	public final static boolean CHANNEL_TRANSACTED = true;
    	public final static boolean REQUEST_REJECTED = false;
    }
}
