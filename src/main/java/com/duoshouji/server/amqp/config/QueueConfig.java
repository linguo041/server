package com.duoshouji.server.amqp.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public class QueueConfig {
	
	public QueueConfig (String exchangeName, String queueName,
			String routingKey, int consumerNum, boolean autoStart){
		this.exchangeName = exchangeName;
		this.queueName = queueName;
		this.routingKey = routingKey;
		this.consumerNum = consumerNum;
		this.autoStart = autoStart;
	}

	@Getter
	@Setter
	private String exchangeName;
	
	@Getter
	@Setter
	private String queueName;
	
	@Getter
	@Setter
	private String routingKey;
	
	@Getter
	@Setter
	private int consumerNum;
	
	@Getter
	@Setter
	private boolean autoStart;
	
	@Getter
	private boolean channelTrasacted = true;
	
	@Getter
	private boolean requestRejected = false;
}
