# PetAiDemo

Демо-сервис на Spring Boot 4 — учёт ветеринарной клиники: владельцы питомцев, питомцы и их визиты к ветеринару.

Проект создан как учебный пример REST CRUD + JPA-связей + маппинга Entity ↔ DTO через MapStruct.

## Стек

- Java 21, Spring Boot 4.1.1
- Spring Data JPA (Hibernate) + PostgreSQL
- Spring MVC (REST) + Bean Validation
- MapStruct (мапперы Entity ↔ DTO)
- Docker Compose (`spring-boot-docker-compose`) — автозапуск Postgres
- Maven (обёртка `mvnw`)

## Доменная модель

```
Owner (владелец) 1 --- * Pet (питомец) 1 --- * Visit (визит к ветеринару)
```

## Быстрый старт

Понадобится: JDK 21, Docker (Docker Desktop должен быть запущен).

```bash
git clone http://localhost:8929/root/petaidemo.git
cd petaidemo
./mvnw spring-boot:run
```

Postgres из `compose.yaml` поднимется автоматически (Docker Compose support), приложение стартует на `http://localhost:8081`.

Собрать jar и запустить отдельно:

```bash
./mvnw clean install
java -jar target/PetAiDemo-0.0.1-SNAPSHOT.jar
```

Настройки подключения к БД — в `src/main/resources/application.yml`.

## REST API

Базовый путь: `http://localhost:8081/api`

| Метод  | Путь                     | Описание                          |
|--------|--------------------------|------------------------------------|
| GET    | `/owners`                | список владельцев                  |
| GET    | `/owners/{id}`           | владелец по id                     |
| GET    | `/owners/{id}/pets`      | питомцы владельца                  |
| POST   | `/owners`                | создать владельца                  |
| PUT    | `/owners/{id}`           | обновить владельца                 |
| DELETE | `/owners/{id}`           | удалить владельца                  |
| GET    | `/pets`                  | список питомцев                    |
| GET    | `/pets/{id}`             | питомец по id                      |
| GET    | `/pets/{id}/visits`      | визиты питомца                     |
| POST   | `/pets`                  | создать питомца                    |
| PUT    | `/pets/{id}`             | обновить питомца                   |
| DELETE | `/pets/{id}`             | удалить питомца                    |
| GET    | `/visits`                | список визитов                     |
| GET    | `/visits/{id}`           | визит по id                        |
| POST   | `/visits`                | создать визит                      |
| PUT    | `/visits/{id}`           | обновить визит                     |
| DELETE | `/visits/{id}`           | удалить визит                      |

Пример создания владельца:

```bash
curl -X POST http://localhost:8081/api/owners \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Ivan","lastName":"Petrov","email":"ivan@example.com","phone":"+79001234567"}'
```

## Тесты

```bash
./mvnw test
```
