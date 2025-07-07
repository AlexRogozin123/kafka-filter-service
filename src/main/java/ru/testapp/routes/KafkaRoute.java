package ru.testapp.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;
import ru.testapp.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;

import static ru.testapp.routes.Endpoints.USERS_ENDPOINT;

@Component
public class KafkaRoute extends RouteBuilder {

    @Override
    public void configure() {
        from(USERS_ENDPOINT)
                .log("Получен запрос: ${body}")
                .unmarshal()
                .json(JsonLibrary.Jackson, List.class)
                .process(exchange -> exchange.setProperty("errors", new ArrayList<String>()))
                .split(body())
                .process(UserUtils::processUser)
                .setHeader(KafkaConstants.KEY, simple("${body.name}"))
                .log("Отправляем в Kafka. Ключ: ${header.kafka.KEY}, Тело: ${body}")
                .to("kafka:user-topic?brokers=localhost:29092")
                .end()
                .process(exchange -> {
                    List<String> errors = exchange.getProperty("errors", List.class);
                    if (errors != null && !errors.isEmpty()) {
                        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
                        exchange.getIn().setBody(UserUtils.generateErrorMessage(errors));
                    }
                });
    }

}
