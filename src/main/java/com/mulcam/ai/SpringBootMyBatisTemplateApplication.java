package com.mulcam.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@EnableConfigurationProperties(
        {FileUploadProperties.class}
)
@SpringBootApplication
public class SpringBootMyBatisTemplateApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootMyBatisTemplateApplication.class, args);
	}
	
	@Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
