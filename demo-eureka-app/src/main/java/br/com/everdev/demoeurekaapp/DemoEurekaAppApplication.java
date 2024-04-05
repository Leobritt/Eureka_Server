package br.com.everdev.demoeurekaapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableEurekaServer
public class DemoEurekaAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoEurekaAppApplication.class, args);
	}

}
