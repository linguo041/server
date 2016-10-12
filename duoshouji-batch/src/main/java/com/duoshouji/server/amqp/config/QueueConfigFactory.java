package com.duoshouji.server.amqp.config;

import com.duoshouji.server.amqp.config.AmqpConfig.ElasticNoteImportMqConfig;
import com.duoshouji.server.amqp.config.AmqpConfig.NoteChangeMqConfig;
import com.duoshouji.server.amqp.config.AmqpConfig.SimpleConsumerConfig;
import com.google.common.collect.Lists;

public class QueueConfigFactory {

	public static QueueConfig loadNoteChangeQueueConfig () {
		return new QueueConfig(NoteChangeMqConfig.EXCHANGE_NAME,
				Lists.newArrayList(NoteChangeMqConfig.QUEUE_NAME), null, NoteChangeMqConfig.CONSUMER_NUM,
				NoteChangeMqConfig.CONSUMER_AUTO_START);
	}
	
	public static QueueConfig loadSimpleQueueConfig () {
		return new QueueConfig(SimpleConsumerConfig.EXCHANGE_NAME,
				Lists.newArrayList(SimpleConsumerConfig.QUEUE_NAME), null, SimpleConsumerConfig.CONSUMER_NUM,
				SimpleConsumerConfig.CONSUMER_AUTO_START);
	}
	
	public static QueueConfig loadElasticNoteImportQueueConfig () {
		return new QueueConfig(ElasticNoteImportMqConfig.EXCHANGE_NAME,
				Lists.newArrayList(ElasticNoteImportMqConfig.QUEUE_NAME), null, ElasticNoteImportMqConfig.CONSUMER_NUM,
				ElasticNoteImportMqConfig.CONSUMER_AUTO_START);
	}
}
