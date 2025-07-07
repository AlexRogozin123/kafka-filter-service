# kafka-filter-service

## Описание
Микросервис на Spring Boot + Apache Camel для фильтрации и валидации пользователей, с отправкой валидных данных в Kafka. Используется Jetty HTTP endpoint для приёма данных, фильтрация по роли и валидация возраста/имени, отправка в Kafka-топик `user-topic`. Есть docker-compose для локального запуска Kafka и Kafka-UI.

## Архитектура
- **Spring Boot** — основной фреймворк
- **Apache Camel** — маршрутизация, интеграция с Kafka и HTTP
- **Kafka** — брокер сообщений
- **Kafka-UI** — веб-интерфейс для просмотра топиков
- **Jetty** — HTTP endpoint `/users`

### Основной flow:
1. POST /users (JSON-массив пользователей)
2. Валидация: имя не пустое, возраст 0-100, роль = USER
3. Валидные пользователи отправляются в Kafka (`user-topic`)
4. Camel-консьюмер читает из Kafka и логирует сообщения

## Быстрый старт

### 1. Запуск инфраструктуры
```bash
docker-compose up -d
```

### 2. Сборка и запуск приложения
```bash
mvn clean package
java -jar target/kafka-filter-service-1.0-SNAPSHOT.jar
```

### 3. Пример запроса
```bash
curl -X POST \
  http://localhost:8080/users \
  -H 'Content-Type: application/json' \
  -d '[{"name":"Vasya","age":30,"role":"USER"}, {"name":"Petya","age":150,"role":"ADMIN"}]'
```

- Только валидные пользователи с ролью USER попадут в Kafka
- Ошибки валидации вернутся с HTTP 400

### 4. Просмотр сообщений
- Открой [http://localhost:8180](http://localhost:8180) (Kafka-UI)
- Топик: `user-topic`

## Переменные и настройки
- Kafka слушает на `localhost:9092`
- HTTP endpoint: `http://localhost:8080/users`
- Все настройки — в `docker-compose.yml` и `application.properties`

## Зависимости
- Java 17+
- Maven
- Docker

---

**Автор:** @AlexRogozin123