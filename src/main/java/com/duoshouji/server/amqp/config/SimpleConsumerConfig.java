package com.duoshouji.server.amqp.config;

public class SimpleConsumerConfig {
	public final static String EXCHANGE_NAME = "duoshouji.exchange.simple.v1";
	public final static String QUEUE_NAME = "duoshouji.queue.simple.v1";
	public final static int CONSUMER_NUM = 5;
	public final static boolean CONSUMER_AUTO_START = true;
	public final static boolean CHANNEL_TRANSACTED = true;
	public final static boolean REQUEST_REJECTED = false;
}
