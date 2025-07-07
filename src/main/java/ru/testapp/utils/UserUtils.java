package ru.testapp.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;

import ru.testapp.dto.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.testapp.utils.Constants.USER_ROLE_NAME;

@Slf4j
public class UserUtils {

    public static void processUser(Exchange exchange) {
        Map<String, Object> map = exchange.getIn().getBody(Map.class);
        User user = User.builder()
                .name((String) map.get("name"))
                .age((Integer) map.get("age"))
                .role((String) map.get("role"))
                .build();
        exchange.getIn().setBody(user);
        if (UserUtils.isUser(user) && UserUtils.isUserValid(user, exchange)) {
            exchange.getIn().setBody(user);
        } else {
            exchange.setRouteStop(true);
        }
    }

    private static boolean isUser(User user) {
        return USER_ROLE_NAME.equalsIgnoreCase(user.getRole());
    }

    private static boolean isUserValid(User user, Exchange exchange) {
        List<String> errors = new ArrayList<>();

        if (user.getName() == null || user.getName().isBlank()) {
            errors.add("некорректное имя");
        }
        if (user.getAge() < 0 || user.getAge() > 100) {
            errors.add("некорректный возраст");
        }

        if (!errors.isEmpty()) {
            List<String> exchangeErrors = exchange.getProperty("errors", List.class);
            String errorMsg = user + ": " + String.join(", ", errors);
            exchangeErrors.add(errorMsg);
            return false;
        }
        return true;
    }

    public static String generateErrorMessage(List<String> errors) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < errors.size(); i++) {
            sb.append(errors.get(i)).append("\n");
        }
        return sb.toString();
    }
}
