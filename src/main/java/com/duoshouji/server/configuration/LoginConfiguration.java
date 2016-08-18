package com.duoshouji.server.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(APIConfiguration.class)
public class LoginConfiguration {

}
