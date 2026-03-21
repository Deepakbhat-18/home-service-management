# 🏠 HomeService — Home Service Management System

A full-stack web application for booking and managing home services. Customers browse and book services, providers manage their assigned jobs, and admins oversee the entire platform from a single dashboard.

---

## ✨ Features

- **Customer** — Browse 8 service categories, search by name, book with a preferred date, track booking status in real time
- **Provider** — View assigned jobs, move bookings through a status workflow (Pending → Accepted → In Progress → Completed)
- **Admin** — Live dashboard stats, assign providers to any booking, view all users and bookings platform-wide
- **Auth** — Session-based login with BCrypt password hashing; roles enforced server-side on every endpoint
- **Auto-seeded** — Test users and 8 sample services created automatically on first startup

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.2, Spring Security, Spring Data JPA |
| Database | MySQL 8 (H2 in-memory available for quick testing) |
| API Docs | SpringDoc OpenAPI (Swagger UI at `/swagger-ui.html`) |
| Frontend | HTML5, CSS3 (CSS Variables), Vanilla JS (ES Modules) |
| Auth | HTTP Session cookie (`JSESSIONID`) |
| Build | Maven 3.8+ |
| Java | 17+ |

---

## 📁 Project Structure

```
homeservice_management_system/
├── homeservice_backend/                  # Spring Boot REST API
│   ├── src/main/java/com/homeservice/
│   │   ├── controller/
│   │   │   ├── AuthController.java       # POST /api/auth/*
│   │   │   ├── AppControllers.java       # /api/services/*, /api/bookings/*
│   │   │   └── AdminController.java      # GET /api/admin/*
│   │   ├── service/
│   │   │   ├── UserService.java
│   │   │   ├── BookingService.java
│   │   │   └── ServiceCatalogService.java
│   │   ├── model/
│   │   │   ├── User.java
│   │   │   ├── Service.java
│   │   │   └── ServiceBooking.java
│   │   ├── repository/                   # Spring Data JPA interfaces
│   │   ├── dto/
│   │   │   └── Dto.java                  # All request & response DTOs
│   │   └── config/
│   │       ├── SecurityConfig.java       # CORS, session, cookie filter
│   │       └── DataSeeder.java           # Seeds DB on first startup
│   └── src/main/resources/
│       ├── application.properties        # Main config (port, JPA, session)
│       └── application-local.properties  # DB credentials (gitignored)
│
└── homeservice-frontend/                 # Static frontend
    ├── index.html                        # Single-page app shell
    ├── css/style.css                     # Design tokens & components
    └── js/
        ├── api.js                        # All fetch calls to backend
        └── app.js                        # Routing, state, page rendering
```

---

## 🚀 Setup & Running

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8 running locally

### 1. Clone the repo
```bash
git clone https://github.com/your-username/homeservice_management_system.git
cd homeservice_management_system/homeservice_backend
```

### 2. Create the database
```sql
CREATE DATABASE homeservice;
```

### 3. Set your credentials

Create `src/main/resources/application-local.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=your_password
```

### 4. Run the backend
```bash
./mvnw spring-boot:run
```

Backend starts at `http://localhost:8080`. DataSeeder runs automatically on first startup — check the console for `✅ Users seeded` and `✅ Services seeded`.

### 5. Open the frontend

Open `homeservice-frontend/index.html` with Live Server (VS Code) or any static server. Make sure it runs on one of the allowed CORS origins — default allowed origins are `localhost:5500` and `localhost:5501`.

> **API Docs:** Visit `http://localhost:8080/swagger-ui.html` for the full interactive Swagger UI.

---

## 👥 Test Accounts

Auto-created by `DataSeeder` on first startup (only when the users table is empty):

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@home.com | admin123 |
| Customer | customer@home.com | customer123 |
| Provider | provider@home.com | provider123 |
| Provider 2 | provider2@home.com | provider123 |

---

## 🔌 API Reference

### Auth — `/api/auth`
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/auth/register` | Create account (email, fullname, address, phone, password, role) |
| `POST` | `/auth/login` | Login — sets `JSESSIONID` session cookie |
| `POST` | `/auth/logout` | Invalidate session |
| `GET` | `/auth/me` | Get current logged-in user |

### Services — `/api/services`
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/services` | List all services (public) |
| `GET` | `/services?q=plumb` | Search by name or category |

### Bookings — `/api/bookings`
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| `POST` | `/bookings` | Customer | Create a booking |
| `GET` | `/bookings/my` | Customer | My booking history |
| `GET` | `/bookings/provider` | Provider | My assigned jobs |
| `GET` | `/bookings/all` | Admin | All bookings on platform |
| `PUT` | `/bookings/:id/status` | Provider | Update job status |
| `PUT` | `/bookings/:id/assign` | Admin | Assign a provider to a booking |

### Admin — `/api/admin`
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/admin/stats` | Total bookings, pending count, customer & provider totals |
| `GET` | `/admin/users` | All users (passwords excluded) |
| `GET` | `/admin/providers` | Provider list for the assign dropdown |

---

## 📋 Booking Status Flow

```
Pending → Accepted → In Progress → Completed
              ↓
           Rejected
```

---

## 🌱 Seeded Services

| Service | Category | Price (₹) |
|---------|----------|-----------|
| Electrical Repair | Electrical | 499 |
| Plumbing Service | Plumbing | 399 |
| Home Cleaning | Cleaning | 599 |
| AC Service & Repair | Appliance | 799 |
| Painting Service | Painting | 1,499 |
| Carpentry Work | Carpentry | 699 |
| Pest Control | Pest Control | 899 |
| Appliance Repair | Appliance | 549 |

---

## ⚠️ Notes

- **`ddl-auto=update`** — tables are preserved between restarts. Change to `create-drop` for a completely fresh DB on every run.
- **Passwords** are hashed with BCrypt — plain-text passwords are never stored.
- **CORS** is configured in `SecurityConfig.java`. If your frontend runs on a different port, add it to the `allowedOrigins` list and restart.
- **Session cookies** use `SameSite=Lax` enforced by a servlet filter in `SecurityConfig.java`, which allows cross-port cookie sending on localhost during development.
