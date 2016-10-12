package com.duoshouji.server.amqp.configuration;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.duoshouji.server.amqp.config.AmqpConfig;
import com.duoshouji.server.amqp.config.QueueConfig;
import com.duoshouji.server.amqp.config.QueueConfigFactory;
import com.duoshouji.server.amqp.consume.ConsumerContainer;
import com.duoshouji.server.amqp.consume.ConsumerContainerFactory;
import com.duoshouji.server.amqp.consume.ElasticImportConsumer;
import com.duoshouji.server.amqp.consume.SimpleConsumer;
import com.duoshouji.server.amqp.produce.Producer;
import com.duoshouji.server.amqp.produce.ProducerFactory;
import com.duoshouji.server.elastic.ElasticImportNote;
import com.google.common.collect.Lists;

/**
 * Created by roy.guo on 7/18/16.
 */
@Configuration
public class AMQPConfiguration {
    private final static int CACHED_CHANNEL_SIZE = 5;

    @Bean
    public ConnectionFactory connectionFactory () {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        connectionFactory.setChannelCacheSize(CACHED_CHANNEL_SIZE);
        connectionFactory.setHost(AmqpConfig.RabbitMqConfig.MQ_SERVER_HOST);
        connectionFactory.setUsername(AmqpConfig.RabbitMqConfig.MQ_SERVER_USER);
        connectionFactory.setPassword(AmqpConfig.RabbitMqConfig.MQ_SERVER_PASS);
        return connectionFactory;
    }

    /**
     * Used to dynamicaly change the broker's configurations
     * Used to declare exchange, queue, binding
     *
     * @return
     */
    @Bean
    public RabbitAdmin rabbitAdmin () {
        return new RabbitAdmin(connectionFactory());
    }
    
    // producer
    @Bean
    public ProducerFactory producerFactory () {
    	ProducerFactory producerFactory = new ProducerFactory();
    	producerFactory.setConnectionFactory(connectionFactory());
    	return producerFactory;
    }
    
    @Bean
    public Producer<String> simpleProducer() {
    	return producerFactory().create(QueueConfigFactory.loadSimpleQueueConfig());
    }
    
    @Bean
    public Producer<String> noteChangeProducer() {
    	return producerFactory().create(QueueConfigFactory.loadNoteChangeQueueConfig());
    }
    
    @Bean
    public Producer<ElasticImportNote> elasticNoteImportProducer() {
    	return producerFactory().create(QueueConfigFactory.loadElasticNoteImportQueueConfig());
    }
    
    // consumer
    @Bean
    public ConsumerContainerFactory consumerContainerFactory () {
    	return new ConsumerContainerFactory(connectionFactory(), rabbitAdmin());
    }
    
    @Bean
    SimpleConsumer simpleConsumer () {
    	return new SimpleConsumer();
    }
    
    @Bean
    ConsumerContainer createSimpleConsumerContainer () {
    	return consumerContainerFactory().createConsumerContainer(QueueConfigFactory.loadSimpleQueueConfig(),
    			simpleConsumer());
    }
    
    @Bean
    ElasticImportConsumer elasticImportConsumer() {
    	return new ElasticImportConsumer();
    }
    
    @Bean
    ConsumerContainer createElasticImportConsumerContainer () {
    	return consumerContainerFactory().createConsumerContainer(QueueConfigFactory.loadElasticNoteImportQueueConfig(),
    			elasticImportConsumer());
    }
}
