# PK Corporate ERP System

> Enterprise-grade Textile Manufacturing & Bulk Custom T-Shirt ERP

![PK Corporate](https://img.shields.io/badge/PK%20Corporate-ERP%20System-E10600?style=for-the-badge)
![React](https://img.shields.io/badge/React-18.3-61DAFB?style=flat-square&logo=react)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=flat-square&logo=postgresql)

---

## 🏭 Overview

PK Corporate ERP is a full-stack enterprise resource planning system built for textile manufacturing and bulk custom t-shirt order management. It supports the complete order lifecycle — from agent order creation to final dispatch and invoice generation.

---

## 🧰 Tech Stack

### Frontend
- **React 18** + Vite + TypeScript
- **Tailwind CSS** + ShadCN-style components
- **Zustand** (state management)
- **React Hook Form** + Zod (validation)
- **Framer Motion** (animations)
- **Recharts** (analytics)
- **Axios** with JWT interceptors

### Backend
- **Spring Boot 3.3** (Java 21)
- **Spring Security** + JWT (access + refresh tokens)
- **Spring Data JPA** + Hibernate
- **PostgreSQL** (primary database)
- **Lombok** + MapStruct
- **Springdoc / Swagger UI**
- **Cloudinary** (file storage)

---

## 📁 Project Structure

```
pk-corporate/
├── frontend/                    # React + Vite application
│   ├── src/
│   │   ├── components/
│   │   │   ├── layout/          # Sidebar, Header, DashboardLayout
│   │   │   ├── ui/              # StatCard, DataTable, Modal, Badge
│   │   │   └── shared/          # ProtectedRoute
│   │   ├── pages/
│   │   │   ├── auth/            # Login, ForgotPassword, ResetPassword
│   │   │   ├── admin/           # All admin module pages
│   │   │   ├── agent/           # Agent dashboard, catalog, orders
│   │   │   ├── accountant/      # Payments, invoices, reports
│   │   │   └── designer/        # Design orders, mockup management
│   │   ├── store/               # Zustand stores (auth, theme)
│   │   ├── services/            # Axios API clients
│   │   └── hooks/               # Custom React hooks
│   ├── Dockerfile
│   └── nginx.conf
│
├── backend/                     # Spring Boot application
│   └── src/main/java/com/pkcorporate/
│       ├── config/              # Security, Cloudinary, Audit, DataSeeder
│       ├── controller/          # REST API controllers
│       ├── service/             # Business logic layer
│       ├── repository/          # JPA repositories
│       ├── entity/              # JPA entities
│       ├── dto/                 # Request/Response DTOs
│       ├── security/            # JWT filter + service
│       ├── exception/           # Global exception handling
│       └── enums/               # Role, OrderStatus, PaymentStatus
│
├── docker-compose.yml
├── .env.example
└── README.md
```

---

## 🗃️ Database Schema

### Core Tables
| Table | Description |
|-------|-------------|
| `users` | All user accounts (admin, agent, accountant, designer) |
| `customers` | Customer profiles linked to agents |
| `orders` | Main order table with full lifecycle |
| `order_items` | Line items per order (product + size/qty) |
| `tshirt_products` | Product catalog |
| `product_images` | Cloudinary image references |
| `payments` | Payment records (advance + balance) |
| `invoices` | GST invoices |
| `production` | Production tracking |
| `dispatch` | Dispatch + logistics |
| `inventory` | Fabric & material stock |
| `commissions` | Agent commission tracking |
| `notifications` | In-app notification system |
| `audit_logs` | Full audit trail |

---

## 🔄 Order Workflow

```
1. PENDING           → Agent creates order
2. PAYMENT_PENDING   → Customer pays 70% advance
3. PAYMENT_VERIFIED  → Accountant verifies payment
4. DESIGN_IN_PROGRESS→ Designer creates mockup
5. DESIGN_APPROVED   → Admin/Customer approves design
6. PRODUCTION        → Factory starts manufacturing
7. QUALITY_CHECK     → QC inspection
8. DISPATCH_READY    → Packed & ready to ship
9. DISPATCHED        → Shipped with tracking
10. COMPLETED        → Delivered + 30% balance collected
```

---

## 🚀 Getting Started

### Prerequisites
- Node.js 20+
- Java 21+
- PostgreSQL 16+
- Maven 3.9+
- Docker (optional)

### 1. Clone & Configure
```bash
git clone <repo-url>
cd pk-corporate
cp .env.example .env
# Edit .env with your credentials
```

### 2. Frontend Setup
```bash
cd frontend
npm install
cp .env.example .env.local
# Set VITE_API_URL=http://localhost:8080/api
npm run dev
```
Frontend starts at: http://localhost:3000

### 3. Backend Setup
```bash
cd backend
# Create PostgreSQL database
createdb pk_corporate

# Run the application
./mvnw spring-boot:run
```
Backend starts at: http://localhost:8080/api
Swagger UI: http://localhost:8080/api/swagger-ui.html

### 4. Docker Setup (Recommended)
```bash
# Copy and configure .env
cp .env.example .env

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f backend
```

---

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Access |
|--------|----------|--------|
| POST | `/auth/login` | Public |
| POST | `/auth/logout` | Auth |
| POST | `/auth/refresh-token` | Public |
| POST | `/auth/forgot-password` | Public |
| POST | `/auth/reset-password` | Public |
| POST | `/auth/change-password` | Auth |
| GET | `/auth/me` | Auth |

### Orders
| Method | Endpoint | Access |
|--------|----------|--------|
| POST | `/v1/orders` | Admin, Agent |
| GET | `/v1/orders` | Admin |
| GET | `/v1/orders/my` | Agent |
| GET | `/v1/orders/{id}` | Auth |
| GET | `/v1/orders/track/{token}` | Public |
| PATCH | `/v1/orders/{id}/status` | Admin, Accountant |
| POST | `/v1/orders/{id}/upload-logo` | Admin, Agent |
| POST | `/v1/orders/{id}/upload-mockups` | Admin, Designer |

---

## 🎨 Role Permissions

| Feature | Admin | Agent | Accountant | Designer |
|---------|-------|-------|------------|----------|
| Dashboard | ✅ | ✅ | ✅ | ✅ |
| All Orders | ✅ | ❌ | ✅ | ❌ |
| My Orders | ✅ | ✅ | ❌ | ❌ |
| Create Order | ✅ | ✅ | ❌ | ❌ |
| Verify Payment | ✅ | ❌ | ✅ | ❌ |
| Generate Invoice | ✅ | ❌ | ✅ | ❌ |
| Upload Mockup | ✅ | ❌ | ❌ | ✅ |
| Manage Users | ✅ | ❌ | ❌ | ❌ |
| View Analytics | ✅ | ❌ | ✅ | ❌ |
| Manage Products | ✅ | ❌ | ❌ | ❌ |
| Manage Inventory | ✅ | ❌ | ❌ | ❌ |

---

## 📋 Business Rules

- **MOQ**: Minimum 10 t-shirts per order (enforced frontend + backend)
- **Payment**: 70% advance required before production
- **GST**: 5% on textile orders (configurable)
- **Commission**: Agent commission rate configurable per agent
- **Tokens**: JWT access (15 min) + refresh (7 days)

---

## 🏗️ Production Deployment

### AWS / VPS
```bash
# Build frontend
cd frontend && npm run build

# Build backend JAR
cd backend && ./mvnw package -DskipTests

# Run with environment vars
java -jar target/pk-corporate-erp-1.0.0.jar \
  --spring.datasource.url=$DB_URL \
  --jwt.secret=$JWT_SECRET
```

### Environment Checklist
- [ ] PostgreSQL database created
- [ ] JWT secret is 256+ bits
- [ ] Cloudinary credentials set
- [ ] SMTP credentials configured
- [ ] Razorpay keys configured
- [ ] CORS origins configured for production domain
- [ ] SSL/TLS certificate configured

---

## 📄 License

© 2025 PK Coporate. All rights reserved. Proprietary software.
