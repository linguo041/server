package com.duoshouji.server.elasticsearch.configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.duoshouji.server.elasticsearch.repository")
@PropertySource("classpath:properties/elasticsearch.properties")
public class ElasticSearchConfiguration {
	private final static int ESNOTE_IMPORT_THREAD_SIZE = 4;
	
	@Autowired
    Environment env;
	
	@Bean
	public ExecutorService executorService () {
		return Executors.newFixedThreadPool(ESNOTE_IMPORT_THREAD_SIZE);
	}
	
//	@Bean
	public NodeClient nodeClient() {
		return (NodeClient) NodeBuilder.nodeBuilder()
				.clusterName(env.getProperty("es.clusterName"))
				.local(true)
				.node()
				.client();
	}
	
	@Bean
	public Client transportClient () throws UnknownHostException {
		Settings settings = Settings.settingsBuilder()
		        .put("cluster.name", env.getProperty("es.clusterName"))
//		        .put("path.home", env.getProperty("es.path.home"))
		        .build();
		Client client = TransportClient.builder().settings(settings)
				.build()
				.addTransportAddress(new InetSocketTransportAddress(
						InetAddress.getByName(env.getProperty("es.host")),
						Integer.parseInt(env.getProperty("es.port"))));
		
		return client;
	}
	
	@Bean
    public ElasticsearchTemplate elasticsearchTemplate() throws UnknownHostException {
        return new ElasticsearchTemplate(transportClient());
    }
}
