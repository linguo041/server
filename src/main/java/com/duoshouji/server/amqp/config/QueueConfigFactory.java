package com.duoshouji.server.amqp.config;

import com.duoshouji.server.amqp.config.AmqpConfig.NoteChangeMqConfig;
import com.duoshouji.server.amqp.config.AmqpConfig.SimpleConsumerConfig;
import com.google.common.collect.Lists;

public class QueueConfigFactory {

	public static QueueConfig loadNodeChangeQueueConfig () {
		return new QueueConfig(NoteChangeMqConfig.EXCHANGE_NAME,
				Lists.newArrayList(NoteChangeMqConfig.QUEUE_NAME), null, NoteChangeMqConfig.CONSUMER_NUM,
				NoteChangeMqConfig.CONSUMER_AUTO_START);
	}
	
	public static QueueConfig loadSimpleQueueConfig () {
		return new QueueConfig(SimpleConsumerConfig.EXCHANGE_NAME,
				Lists.newArrayList(SimpleConsumerConfig.QUEUE_NAME), null, SimpleConsumerConfig.CONSUMER_NUM,
				SimpleConsumerConfig.CONSUMER_AUTO_START);
	}
}
