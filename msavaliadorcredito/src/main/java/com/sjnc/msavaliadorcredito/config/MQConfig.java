package com.sjnc.msavaliadorcredito.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

        @Value("${mq.queues.emissao-catoes}")
        private String emissaoCataosFila;

      @Bean
        public Queue queueEmissaoCartoes(){
                return new Queue(emissaoCataosFila,true);
            }
}
