# 3D Marketplace (MVP)

Цей проєкт — бекенд-ядро біржі для торгівлі 3D-візуалізаціями.  
**Головна мета:** здобуття комерційних навичок розробки на Java/Spring Boot.

## 🛠 Технологічний стек
* **Backend:** Java 21, Spring Boot 4.1.1 (Web, Data JPA, Validation, Security), Maven.
* **Security:** JWT (JSON Web Token) Stateless Auth, JJWT, BCrypt.
* **Database:** PostgreSQL, Flyway (Migration).
* **API Documentation:** OpenAPI / Swagger 3 (springdoc-openapi 2.7.0).
* **Testing:** JUnit 5, Mockito, Testcontainers.

## 🛤 Хронологія розробки (Roadmap)

### Етап 1. Базовий рівень (Дані)
1. **Інфраструктура:** Піднято PostgreSQL через Docker Compose.
2. **Міграції:** Налаштовано Flyway для версіонування схем БД (V1__init_schema.sql).
3. **Сутності (Entities):** Створено базові класи `User`, `Wallet`.

### Етап 2. Реєстрація та валідація
4. **DTO:** Створено об'єкти передачі даних (`UserRegistrationDto`, `UserLoginDto`) із правилами `jakarta.validation`.
5. **Бізнес-логіка:** Написано `UserService` для збереження юзера та автоматичного створення його гаманця.
6. **Обробка помилок:** Реалізовано `GlobalExceptionHandler` (`@RestControllerAdvice`) для перехоплення помилок валідації та конфліктів БД (наприклад, дублікат email) і перетворення їх у зрозумілий JSON.

### Етап 3. Ядро безпеки (Spring Security & JWT)
7. **Шифрування:** Підключено `BCryptPasswordEncoder` для хешування паролів.
8. **Генерація токенів:** Створено `JwtService` для криптографічного підпису JWT (алгоритм HMAC-SHA) зі строком дії 24 години.
9. **Інтеграція сутностей:** Клас `User` імплементував інтерфейс `UserDetails`, а `CustomUserDetailsService` навчив Spring шукати користувачів у нашій БД за email-ом через `orElseThrow`.
10. **Фільтрація запитів:** Створено `JwtAuthenticationFilter` (`OncePerRequestFilter`), який перехоплює HTTP-запити, відрізає префікс "Bearer ", перевіряє підпис токена і кладе користувача у `SecurityContextHolder`.
11. **Конфігурація (SecurityConfig):** Заборонено серверні сесії (`STATELESS`), відкрито публічні шляхи (`/auth/**`, `/v3/api-docs`), а фільтр JWT вбудовано в офіційний ланцюг Spring Security перед стандартними фільтрами.
12. **Документація:** Подолано конфлікт версій Spring Boot 3 та Swagger, налаштовано публічний доступ до UI документації.

### Етап 4. Бізнес-ядро (Замовлення)
13. **Сутності:** Створено `Order` з використанням Enum (`OrderStatus`).
14. **Проблема N+1:** Налаштовано `FetchType.LAZY` для всіх зв'язків. Використання магічних методів Spring Data JPA (`findAllByCustomer_Email`) для агрегації.
15. **State Machine (Кінцевий автомат):** Реалізовано переходи станів замовлення (створення -> взяття в роботу -> завершення).

### Етап 5. Фінансове ядро (Транзакції)
16. **Консистентність даних:** Створено `Transaction` як Immutable Audit Log (незмінний лог) руху коштів.
17. **Race Conditions:** Застосовано Optimistic Locking (`@Version` у `Wallet`) для запобігання подвійному витрачанню коштів (Double-spending).
18. **ACID:** Інтегровано транзакційні сервіси (Transaction Propagation) для гарантії принципу "або все, або нічого" при переказі грошей.

## 🗄 Структура Бази Даних (ER Diagram)
```mermaid
erDiagram
    USER ||--o| WALLET : "has (1:1)"
    USER ||--o{ ORDER : "creates (1:M)"
    USER ||--o{ ORDER : "executes (1:M)"
    USER ||--o{ TRANSACTION : "sends (1:M)"
    USER ||--o{ TRANSACTION : "receives (1:M)"
    ORDER ||--o{ TRANSACTION : "has (1:M)"

    USER {
        UUID id PK
        VARCHAR display_name
        VARCHAR email
        VARCHAR password
        VARCHAR role
        TIMESTAMP created_at
        TIMESTAMP updated_at
        TIMESTAMP deleted_at
    }
    WALLET {
        UUID id PK
        UUID user_id FK
        DECIMAL balance
        INTEGER version
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    ORDER {
        UUID id PK
        TEXT description
        DECIMAL price
        UUID customer_id FK
        UUID executor_id FK
        VARCHAR status
        TIMESTAMP deadline
        TIMESTAMP created_at
        TIMESTAMP finished_at
    }
    TRANSACTION {
        UUID id PK
        UUID order_id FK
        UUID sender_id FK
        UUID receiver_id FK
        DECIMAL amount
        VARCHAR status
        VARCHAR payment_method
        TIMESTAMP created_at
    }