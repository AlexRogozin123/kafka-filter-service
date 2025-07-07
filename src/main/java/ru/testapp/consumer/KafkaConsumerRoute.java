package ru.testapp.consumer;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerRoute extends RouteBuilder {
    @Override
    public void configure() {
        from("kafka:user-topic?brokers=localhost:9092")
                .log("Получено сообщение из Kafka: ${body}");
    }
}