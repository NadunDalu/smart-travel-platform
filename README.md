# Smart Travel Booking Platform

## 🏗️ Architecture Overview

This is a distributed microservices-based travel booking platform built with Spring Boot 3, Java 17, and MySQL.

### Services
1. **User Service** (Port 8081) - Manages user information
2. **Flight Service** (Port 8082) - Manages flight details and availability
3. **Hotel Service** (Port 8083) - Manages hotel details and availability
4. **Booking Service** (Port 8084) - Main orchestrator for booking workflow
5. **Payment Service** (Port 8085) - Handles payment processing
6. **Notification Service** (Port 8086) - Sends notifications to users

### Communication Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                      BOOKING FLOW                                │
└─────────────────────────────────────────────────────────────────┘

1. User sends booking request to Booking Service

2. Booking Service → User Service (WebClient)
   - Validates user exists and is active

3. Booking Service → Flight Service (Feign Client)
   - Checks flight availability

4. Booking Service → Hotel Service (Feign Client)
   - Checks hotel availability

5. Booking Service
   - Calculates total cost
   - Stores booking as PENDING

6. Booking Service → Notification Service (WebClient)
   - Sends booking created notification

7. Payment Service → Booking Service (WebClient)
   - Processes payment
   - Updates booking status to CONFIRMED

8. Booking Service → Notification Service (WebClient)
   - Sends booking confirmed notification
```

### Architecture Diagram

```
┌──────────────┐
│   Client     │
└──────┬───────┘
       │
       ▼
┌──────────────────────────────────────────────────────┐
│              Booking Service (8084)                  │
│              (Main Orchestrator)                     │
└───┬──────────┬──────────┬──────────┬────────────────┘
    │          │          │          │
    │ WebClient│ Feign    │ Feign    │ WebClient
    │          │          │          │
    ▼          ▼          ▼          ▼
┌────────┐ ┌────────┐ ┌────────┐ ┌──────────────┐
│  User  │ │ Flight │ │ Hotel  │ │ Notification │
│Service │ │Service │ │Service │ │   Service    │
│ (8081) │ │ (8082) │ │ (8083) │ │   (8086)     │
└────────┘ └────────┘ └────────┘ └──────────────┘
                                         ▲
    ┌────────────────────────────────────┘
    │ WebClient
    │
┌───┴────────┐
│  Payment   │
│  Service   │
│  (8085)    │
└────────────┘
```

## 🚀 Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Postman (for testing)

### Step 1: Database Setup

Create 6 MySQL databases:

```sql
CREATE DATABASE user_db;
CREATE DATABASE flight_db;
CREATE DATABASE hotel_db;
CREATE DATABASE booking_db;
CREATE DATABASE payment_db;
CREATE DATABASE notification_db;
```

### Step 2: Clone and Setup

```bash
# Create project directory
mkdir smart-travel-platform
cd smart-travel-platform

# Create 6 service directories
mkdir user-service flight-service hotel-service booking-service payment-service notification-service
```

### Step 3: Configure Database Password

Update `application.properties` in each service:

```properties
spring.datasource.password=your_mysql_password
```

### Step 4: Build All Services

```bash
# Build each service
cd user-service
mvn clean install
cd ..

cd flight-service
mvn clean install
cd ..

cd hotel-service
mvn clean install
cd ..

cd notification-service
mvn clean install
cd ..

cd payment-service
mvn clean install
cd ..

cd booking-service
mvn clean install
cd ..
```

### Step 5: Run All Services

**Important:** Start services in this order:

```bash
# Terminal 1 - User Service
cd user-service
mvn spring-boot:run

# Terminal 2 - Flight Service
cd flight-service
mvn spring-boot:run

# Terminal 3 - Hotel Service
cd hotel-service
mvn spring-boot:run

# Terminal 4 - Notification Service
cd notification-service
mvn spring-boot:run

# Terminal 5 - Payment Service
cd payment-service
mvn spring-boot:run

# Terminal 6 - Booking Service (start last)
cd booking-service
mvn spring-boot:run
```



## 🔧 Technology Stack

- **Framework:** Spring Boot 3.2.0
- **Language:** Java 17
- **Database:** MySQL 8.0
- **Communication:**
  - Spring Cloud OpenFeign (Feign Client)
  - Spring WebFlux (WebClient)
  - REST APIs
- **ORM:** Spring Data JPA
- **Build Tool:** Maven
- **Libraries:** Lombok

## 📊 Communication Technologies Used

### Feign Client
- Booking Service → Flight Service
- Booking Service → Hotel Service

### WebClient
- Booking Service → User Service
- Booking Service → Notification Service
- Payment Service → Booking Service

## 🐛 Troubleshooting

### Service Won't Start
- Check if the port is already in use
- Verify MySQL is running
- Check database credentials in application.properties

### Feign Client Error
- Ensure Flight and Hotel services are running
- Check the URLs in booking-service application.properties

### WebClient Timeout
- Increase timeout in application.properties
- Verify target service is responding

### Database Connection Error
- Verify MySQL is running
- Check database exists
- Verify username/password


## 👨‍💻 Author

Assignment for ITS 4243 - Microservices and Cloud Computing
University of Sri Jayewardenepura
Student Name: Daluwatta H.N.S.
Student ID: ICT/21/820
