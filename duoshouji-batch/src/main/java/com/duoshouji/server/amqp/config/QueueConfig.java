package com.duoshouji.server.amqp.config;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class QueueConfig {
	
	public QueueConfig (String exchangeName, List<String> queueNames,
			String routingKey, int consumerNum, boolean autoStart){
		this.exchangeName = exchangeName;
		this.queueNames = queueNames;
		this.routingKey = routingKey;
		this.consumerNum = consumerNum;
		this.autoStart = autoStart;
	}

	@Getter
	@Setter
	private String exchangeName;
	
	@Getter
	@Setter
	private List<String> queueNames;
	
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
