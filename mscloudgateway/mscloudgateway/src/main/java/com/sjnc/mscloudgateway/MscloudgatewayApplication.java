package com.sjnc.mscloudgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
public class MscloudgatewayApplication {

	public static void main(String[] args) {

		SpringApplication.run(MscloudgatewayApplication.class, args);
	}

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder){
		return builder
				.routes()
					.route(r -> r.path("/clientes/**").uri("lb://msclientes")) // lb: load balance
					.route(r -> r.path("/cartoes/**").uri("lb://mscartoes")) // lb: load balance
					.route(r -> r.path("/avaliacoes-credito/**").uri("lb://msavaliadorcredito")) // lb: load balance
				.build();
	// sempre que for criado um microserviço no projeto e registrar com o eureka é possível vir no gateway e registrar ele tb para faze o balanceamento de carga

	}
}
