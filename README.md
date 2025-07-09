# Netty Server

Высокопроизводительный асинхронный сервер на базе Netty с поддержкой HTTP и WebSocket соединений.

## Возможности

- ✅ HTTP и WebSocket поддержка
- ✅ SSL/TLS шифрование
- ✅ JWT аутентификация
- ✅ CORS настройки
- ✅ Настраиваемые буферы и таймауты
- ✅ Graceful shutdown
- ✅ Heartbeat мониторинг
- ✅ Метрики и логирование
- ✅ **Rate Limiting** - защита от DDoS атак
- ✅ **Health Check** - мониторинг состояния приложения
- ✅ **Structured Logging** - структурированное логирование с MDC
- ✅ **Request Tracing** - трейсинг запросов
- ✅ **Global Error Handling** - централизованная обработка ошибок
- ✅ **Configuration Files** - конфигурация через YAML

## Быстрый старт

### Maven зависимость

```xml
<dependency>
    <groupId>com.ob</groupId>
    <artifactId>server-netty</artifactId>
    <version>1.0.28</version>
</dependency>
```

### Базовый HTTP сервер

```java
import com.ob.server.*;

public class BasicHttpServer {
    public static void main(String[] args) {
        RequestSessionFactory sessionFactory = new MySessionFactory();
        
        Server server = new Server.ServerBuilder(8080, sessionFactory)
            .setHttp()
            .setCorsConfig(CorsConfig.withAnyOrigin().build())
            .build();
            
        server.start();
    }
}
```

### WebSocket сервер

```java
public class WebSocketServer {
    public static void main(String[] args) {
        RequestSessionFactory sessionFactory = new MySessionFactory();
        
        Server server = new Server.ServerBuilder(8080, sessionFactory)
            .setWebsocket()
            .setSecurityHandler(new SecurityHandler(new JWTSecurityProcessor("secret")))
            .build();
            
        server.start();
    }
}
```

### SSL сервер

```java
public class SecureServer {
    public static void main(String[] args) {
        RequestSessionFactory sessionFactory = new MySessionFactory();
        
        Server server = new Server.ServerBuilder(8443, sessionFactory)
            .setWebsocket()
            .setCertificate("/path/to/key.pem", "/path/to/cert.pem")
            .setSecurityHandler(new SecurityHandler(new JWTSecurityProcessor("secret")))
            .build();
            
        server.start();
    }
}
```

### Сервер с новыми возможностями

```java
public class AdvancedServer {
    public static void main(String[] args) {
        RequestSessionFactory sessionFactory = new MySessionFactory();
        
        Server server = new Server.ServerBuilder(8080, sessionFactory)
            .setWebsocket()
            .setCorsConfig(CorsConfig.withAnyOrigin().build())
            .setSecurityHandler(new SecurityHandler(new JWTSecurityProcessor("secret")))
            .setHandlers(() -> new ChannelHandler[]{
                new RequestTracingHandler(),      // Трейсинг запросов
                new RateLimitHandler(1000.0),     // Rate limiting
                new HealthCheckHandler("1.0.28", "my-app"),
                new GlobalErrorHandler()          // Обработка ошибок
            })
            .build();
            
        server.start();
    }
}
```

## Конфигурация

### Настройка буферов

```java
Server server = new Server.ServerBuilder(8080, sessionFactory)
    .setReceiveBuffer(64 * 1024)      // 64KB receive buffer
    .setSendBuffer(128 * 1024)        // 128KB send buffer
    .setWriteBufferWaterMarkLow(16 * 1024)   // 16KB low water mark
    .setWriteBufferWaterMarkHigh(64 * 1024)  // 64KB high water mark
    .build();
```

### Настройка потоков

```java
Server server = new Server.ServerBuilder(8080, sessionFactory)
    .setBossNumber(2)     // Количество boss threads
    .setWorkNumber(8)     // Количество worker threads
    .build();
```

### Настройка таймаутов

```java
ServerConfig config = new ServerConfig(8080, sessionFactory);
config.setReaderIdleTime(60);    // 60 секунд
config.setWriterIdleTime(300);   // 5 минут
config.setAllIdleTime(600);      // 10 минут
```

## Безопасность

### JWT аутентификация

```java
JWTSecurityProcessor jwtProcessor = new JWTSecurityProcessor("your-secret-key");
SecurityHandler securityHandler = new SecurityHandler(jwtProcessor);

Server server = new Server.ServerBuilder(8080, sessionFactory)
    .setSecurityHandler(securityHandler)
    .build();
```

### CORS настройки

```java
CorsConfig corsConfig = CorsConfig.withAnyOrigin()
    .allowedRequestMethods(HttpMethod.GET, HttpMethod.POST)
    .allowedRequestHeaders("Authorization", "Content-Type")
    .allowCredentials()
    .build();

Server server = new Server.ServerBuilder(8080, sessionFactory)
    .setCorsConfig(corsConfig)
    .build();
```

## Сессии

### Создание кастомной сессии

```java
public class MySession implements RequestSession {
    private final String sessionId;
    private final ChannelRequestDto request;
    
    public MySession(String sessionId, ChannelRequestDto request) {
        this.sessionId = sessionId;
        this.request = request;
    }
    
    @Override
    public String getSessionId() {
        return sessionId;
    }
    
    @Override
    public ChannelRequestDto getChannelRequest() {
        return request;
    }
    
    @Override
    public void onOpen() {
        System.out.println("Session opened: " + sessionId);
    }
    
    @Override
    public void onClose() {
        System.out.println("Session closed: " + sessionId);
    }
    
    @Override
    public void onRead(ChannelHandlerContext ctx, Object msg) {
        // Обработка входящих сообщений
        System.out.println("Received: " + msg);
    }
}
```

### Фабрика сессий

```java
public class MySessionFactory implements RequestSessionFactory {
    @Override
    public RequestSession createSession(ChannelRequestDto request) {
        String sessionId = UUID.randomUUID().toString();
        return new MySession(sessionId, request);
    }
}
```

## Graceful Shutdown

```java
public class ServerApplication {
    private static Server server;
    
    public static void main(String[] args) {
        server = new Server.ServerBuilder(8080, sessionFactory)
            .setWebsocket()
            .build()
            .start();
            
        // Добавляем shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                server.stop();
                System.out.println("Server stopped gracefully");
            } catch (Exception e) {
                System.err.println("Error stopping server: " + e.getMessage());
            }
        }));
    }
}
```

## Мониторинг

### Метрики

Сервер предоставляет встроенные метрики через `MeterService`:

```java
MeterService meterService = new MeterService() {
    @Override
    public int incrementCounter(String counterName, String... tags) {
        // Увеличить счетчик
        return 0;
    }
    
    @Override
    public int decrementCounter(String counterName, String... tags) {
        // Уменьшить счетчик
        return 0;
    }
    
    @Override
    public void record(String key, long start, String... tags) {
        // Записать метрику времени
    }
};

ServerConfig config = new ServerConfig(8080, sessionFactory);
config.setMeterService(meterService);
```

### Логирование

Сервер использует SLF4J для логирования. Настройте `logback.xml`:

```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <logger name="com.ob.server" level="INFO"/>
    
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

## Производительность

### Рекомендуемые настройки для высоких нагрузок

```java
Server server = new Server.ServerBuilder(8080, sessionFactory)
    .setBossNumber(1)                    // Один boss thread
    .setWorkNumber(Runtime.getRuntime().availableProcessors() * 2)  // 2x CPU cores
    .setReceiveBuffer(64 * 1024)         // 64KB receive buffer
    .setSendBuffer(128 * 1024)           // 128KB send buffer
    .setWriteBufferWaterMarkLow(32 * 1024)   // 32KB low water mark
    .setWriteBufferWaterMarkHigh(128 * 1024) // 128KB high water mark
    .build();
```

## Новые возможности

### Rate Limiting

Защита от DDoS атак с настраиваемыми лимитами:

```java
RateLimitHandler rateLimitHandler = new RateLimitHandler(1000.0); // 1000 req/sec глобально
// или
RateLimitHandler rateLimitHandler = new RateLimitHandler(1000.0, 100.0); // 1000 глобально, 100 на клиента
```

### Health Check

Мониторинг состояния приложения:

- `/health` - базовый статус
- `/health/detailed` - детальная информация
- `/ready` - готовность приложения
- `/live` - жизнеспособность

```java
HealthCheckHandler healthHandler = new HealthCheckHandler("1.0.28", "my-app", new HealthStatusChecker() {
    @Override
    public boolean isReady() {
        // Проверка готовности (БД, кэш, etc.)
        return true;
    }
    
    @Override
    public boolean isAlive() {
        // Проверка жизнеспособности
        return true;
    }
});
```

### Структурированное логирование

Логирование с MDC для трейсинга запросов:

```java
// Автоматически добавляется в каждый запрос
MDC.put("requestId", "uuid");
MDC.put("traceId", "uuid");
MDC.put("method", "GET");
MDC.put("uri", "/api/users");
```

### Глобальная обработка ошибок

Централизованная обработка исключений с JSON ответами:

```json
{
  "code": "BAD_REQUEST",
  "message": "Invalid request format",
  "requestId": "uuid",
  "traceId": "uuid",
  "timestamp": 1234567890
}
```

## Обработка ошибок

Сервер предоставляет несколько типов исключений:

- `BadRequestException` - некорректный запрос
- `UnauthorizedException` - неавторизованный доступ
- `ForbiddenException` - запрещенный доступ
- `TooManyRequestException` - превышен лимит запросов
- `ProtocolException` - ошибка протокола

## Конфигурация

### YAML конфигурация

Создайте `application.yml`:

```yaml
server:
  port: 8080
  rateLimit:
    enabled: true
    globalRequestsPerSecond: 1000
  health:
    enabled: true
    version: "1.0.28"
  logging:
    level: INFO
    file:
      enabled: true
      path: "logs/server.log"
```

## Лицензия

MIT License 