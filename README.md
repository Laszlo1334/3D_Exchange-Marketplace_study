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
13. **Refresh Tokens:** Впроваджено архітектуру довготривалої авторизації. Створено сутність `RefreshToken` (зв'язок 1-до-багатьох з `User`) та оновлено механізм логіну для видачі пари токенів (Access + Refresh) через `JwtAuthResponse`.

### Етап 4. Бізнес-ядро (Замовлення)
14. **Сутності:** Створено `Order` з використанням Enum (`OrderStatus`).
15. **Проблема N+1:** Вирішено проблему N+1 запитів до БД. Налаштовано `FetchType.LAZY` для всіх зв'язків та застосовано оптимізацію через `@EntityGraph` (LEFT JOIN) і `JOIN FETCH` для вибірки агрегованих даних за один SQL-запит.
16. **State Machine (Кінцевий автомат):** Реалізовано переходи станів замовлення (створення -> взяття в роботу -> завершення).

### Етап 5. Фінансове ядро (Транзакції)
17. **Консистентність даних:** Створено `Transaction` як Immutable Audit Log (незмінний лог) руху коштів.
18. **Race Conditions:** Застосовано Optimistic Locking (`@Version` у `Wallet`) для запобігання подвійному витрачанню коштів (Double-spending).
19. **ACID:** Інтегровано транзакційні сервіси (Transaction Propagation) для гарантії принципу "або все, або нічого" при переказі грошей.

### Етап 6. Розширене Фінансове Ядро (Escrow Pattern)
20. **Безпечна угода (Escrow):** Впроваджено розділення балансу на доступний (`balance`) та заморожений (`holdBalance`). Гроші холдуються при створенні замовлення і переказуються лише після успішного завершення.
21. **Зовнішні транзакції:** Реалізовано логіку депозитів (Top-Up) із підтримкою nullable-полів для `sender_id` та `order_id`.

### Етап 7. Подієво-орієнтована архітектура (Event-Driven Architecture)
22. **Декаплінг сервісів:** Застосовано патерн Observer через Spring `ApplicationEventPublisher`. Створення гаманця відв'язано від реєстрації юзера через `@EventListener`.

### Етап 8. Аудит (Data Traceability)
23. **JPA Auditing:** Створено `BaseAuditingEntity` для автоматичного відстеження дат створення/оновлення та ідентифікації авторів змін (`created_by`, `updated_by`).
24. **Ізольований лог операцій:** Створено `AuditService` та таблицю `audit_log` для фіксації критичних дій. Застосовано `@Transactional(propagation = Propagation.REQUIRES_NEW)`, що гарантує збереження логу аудиту в БД незалежно від відкочування (Rollback) основної бізнес-транзакції.

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
        VARCHAR created_by
        VARCHAR updated_by
    }
    WALLET {
        UUID id PK
        UUID user_id FK
        DECIMAL balance
        INTEGER version
        TIMESTAMP created_at
        TIMESTAMP updated_at
        DECIMAL hold_balance
        VARCHAR created_by
        VARCHAR updated_by
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
        TIMESTAMP updated_at
        TIMESTAMP finished_at
        VARCHAR created_by
        VARCHAR updated_by
    }
    TRANSACTION {
        UUID id PK
        UUID order_id FK, Nullable
        UUID sender_id FK, Nullable
        UUID receiver_id FK, Nullable
        DECIMAL amount
        VARCHAR status
        VARCHAR payment_method
        TIMESTAMP created_at
        TIMESTAMP updated_at
        VARCHAR created_by
        VARCHAR updated_by
        VARCHAR type
    }
    REFRESH_TOKEN {
        UUID id PK
        VARCHAR token
        TIMESTAMP expires_at
        UUID user_id FK
    }
    AUDIT_LOG {
        UUID id PK
        VARCHAR action
        VARCHAR description
        VARCHAR user_email
        TIMESTAMP created_at
    }