# Eventora

Eventora е Spring Boot уеб приложение за откриване на събития, закупуване и управление на електронни билети. Проектът следва MVC архитектура с отделни controller, service, repository и domain слоеве.

Публично repository: <https://github.com/lubo96666-art/Eventora-site>

## Технологичен стек

- Java 17
- Spring Boot 3.4.0
- Spring MVC и Thymeleaf
- Spring Data JPA и Hibernate
- Jakarta Bean Validation
- MySQL 8.4 (изпитен профил `mysql`)
- H2 като безопасен локален профил по подразбиране
- Maven, HTML, CSS и JavaScript

## Domain модел

Основните domain entities са `Event`, `Venue` и `TicketOrder`. Всички entities използват UUID идентификатори, имат JPA repository и участват в service логика. Връзките включват Event → Venue и TicketOrder → Event/User.

## Основни функционалности

- пълен CRUD за събития от администраторския панел;
- създаване и редактиране на събитие с избор на зала;
- покупка на един или повече билети и автоматично намаляване на местата;
- отказване на поръчка и възстановяване на свободните места;
- динамичен каталог, отделна страница за събитие, „Моите билети“ и електронен билет;
- регистрация, вход, изход, имейл код за потвърждение и смяна на парола;
- server-side validation с видими грешки към полетата;
- custom exceptions и обща error страница за нарушени бизнес правила;
- session-based access control и отделна проверка за роля `ADMIN`;
- BCrypt хеширане на паролите.

## Страници

- `/visualization` – динамичен каталог и кошница;
- `/events/{id}` – динамични детайли за събитие;
- `/my-tickets` – поръчки на влезлия потребител;
- `/my-tickets/{id}` – електронен билет;
- `/admin/events` – администраторски списък;
- `/admin/events/create` и `/admin/events/{id}/edit` – валидирана CRUD форма.

## Локално стартиране без Docker

Разархивирай проекта в нова празна папка. В IntelliJ IDEA избери **File → Open**, посочи самия `pom.xml` и отвори проекта като Maven project. Изчакай Maven синхронизацията и използвай Java 17. Не отваряй само папката `src` и не копирай проекта върху стара `.idea` конфигурация.

При грешка `package jakarta.servlet.http does not exist` стартирай `CHECK-MAVEN-WINDOWS.bat`, след което натисни **Reload All Maven Projects** в Maven панела на IntelliJ.

След Maven синхронизацията стартирай директно `EventoraApplication.java`. H2 настройките са записани направо в основния `application.properties`, така че не са необходими Docker, MySQL или активен профил.

Отвори `http://localhost:8080/visualization` след съобщението `Started EventoraApplication`.

## Основна изпитна конфигурация с MySQL

```bash
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

Отвори `http://localhost:8080/visualization`.

За изпитното стартиране задай активен профил `mysql`. Настройките могат да се подадат чрез `DB_URL`, `DB_USERNAME` и `DB_PASSWORD`; без променливи приложението очаква MySQL на `localhost:3306` и база `eventora`.

## Тестови профили

Виж `TEST_USERS.txt`. Паролите се съхраняват като BCrypt hash в базата.

## Интеграции

- MySQL чрез Spring Data JPA;
- SMTP/Gmail чрез Spring Mail за потвърждение и възстановяване на парола;
- Docker Compose за локална MySQL среда.

За реално изпращане на имейли задай `MAIL_USERNAME`, `MAIL_PASSWORD` и при нужда `APP_BASE_URL`.

## Покритие на изпитните изисквания

- 3 основни domain entities: `Event`, `Venue`, `TicketOrder`;
- UUID primary key за всяка entity;
- точно 1 JPA repository и поне 1 service за всяка entity;
- повече от 6 Thymeleaf страници, от които поне 4 динамични;
- пълен CRUD за `Event`;
- валидни POST функционалности: създаване, редактиране, изтриване, checkout и отказване на поръчка;
- session-based authentication и role-based достъп за `ADMIN`;
- BCrypt хеширане, Bean Validation, custom exceptions и глобална error страница;
- JPA relationships между `Event`/`Venue` и `TicketOrder`/`Event`/`User`.

## Автоматични тестове

```bash
./mvnw test
```

Тестовете покриват ограниченията за максимум 8 билета, потребителските loyalty точки и основните business rules при управление на събития и поръчки.
