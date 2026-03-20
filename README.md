# Home-Service



## 📝 Description

Home-service is a robust and scalable backend application developed in Java, utilizing Maven for streamlined project management and dependency handling. Designed to serve as a central hub for residential service ecosystems, this project provides a structured foundation for managing home-related tasks, service scheduling, and provider interactions. Its clean architecture and use of the Java ecosystem make it an ideal solution for developers looking to build or integrate reliable service management features into home automation platforms or service marketplaces.

## 🛠️ Tech Stack

Java (Spring Boot), MySQL, HTML, CSS, JavaScript, Maven


## 📦 Key Dependencies

```
spring-boot-starter-web: 2.5.0
```

## 📁 Project Structure

```
.
├── homeservice-frontend
│   ├── css
│   │   └── style.css
│   ├── index.html
│   └── js
│       ├── api.js
│       └── app.js
└── homeservice_backend
    ├── pom.xml
    ├── src
    │   └── main
    │       ├── java
    │       │   └── com
    │       │       └── homeservice
    │       │           ├── HomeServiceApplication.java
    │       │           ├── config
    │       │           │   ├── DataSeeder.java
    │       │           │   └── SecurityConfig.java
    │       │           ├── controller
    │       │           │   ├── AdminController.java
    │       │           │   ├── AppControllers.java
    │       │           │   └── AuthController.java
    │       │           ├── dto
    │       │           │   └── Dto.java
    │       │           ├── model
    │       │           │   ├── Service.java
    │       │           │   ├── ServiceBooking.java
    │       │           │   └── User.java
    │       │           ├── repository
    │       │           │   ├── ServiceBookingRepository.java
    │       │           │   ├── ServiceRepository.java
    │       │           │   └── UserRepository.java
    │       │           └── service
    │       │               ├── BookingService.java
    │       │               ├── ServiceCatalogService.java
    │       │               └── UserService.java
    │       └── resources
    │           └── application.properties
    └── target
        └── classes
            └── com
                └── homeservice
                    ├── dto
                    │   └── Dto.class
                    └── model
                        ├── Service.class
                        ├── ServiceBooking.class
                        └── User.class
```

## 🛠️ Development Setup

### Java (Maven) Setup
1. Install Java (JDK 11+ recommended)
2. Install Maven
3. Install dependencies: `mvn install`
4. Run the project: `mvn exec:java` or check `pom.xml` for specific run commands


## ⚙️ Configuration (Local Setup)

### 📄 application.properties

Located at:

homeservice_backend/src/main/resources/application.properties

```properties
# --- Server ---
server.port=8080

# --- Application ---
spring.application.name=Home Service Management

# --- MySQL Database ---
spring.datasource.url=jdbc:mysql://localhost:3306/homeservice

# --- JPA / Hibernate ---
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect

# --- Profile ---
spring.profiles.active=local

# --- Thymeleaf ---
spring.thymeleaf.cache=false

# --- Logging ---
logging.level.com.homeservice=DEBUG

# --- Session Cookie ---
server.servlet.session.cookie.same-site=Lax
server.servlet.session.cookie.secure=false
server.servlet.session.cookie.http-only=true

# --- Load .env ---
spring.config.import=optional:file:.env[.properties]

### 📄 application-local.properties

Create this file for local development:

homeservice_backend/src/main/resources/application-local.properties

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/homeservice
spring.datasource.username=root
spring.datasource.password=your_password
---
