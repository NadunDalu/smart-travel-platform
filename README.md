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

## 📝 API Testing Guide

### 1. Create a User

```bash
POST http://localhost:8081/api/users
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+94771234567",
  "address": "123 Main St, Colombo"
}
```

**Response:** Note the `id` (e.g., 1)

### 2. Create a Flight

```bash
POST http://localhost:8082/api/flights
Content-Type: application/json

{
  "flightNumber": "SL101",
  "airline": "SriLankan Airlines",
  "origin": "Colombo",
  "destination": "Dubai",
  "departureTime": "2025-01-10 08:00",
  "arrivalTime": "2025-01-10 12:00",
  "price": 45000.00,
  "availableSeats": 50
}
```

**Response:** Note the `id` (e.g., 200)

### 3. Create a Hotel

```bash
POST http://localhost:8083/api/hotels
Content-Type: application/json

{
  "name": "Hilton Colombo",
  "location": "Colombo",
  "address": "2 Sir Chittampalam A Gardiner Mawatha",
  "rating": 5,
  "pricePerNight": 15000.00,
  "availableRooms": 20,
  "amenities": "WiFi, Pool, Gym, Spa"
}
```

**Response:** Note the `id` (e.g., 55)

### 4. Create a Booking (Main Flow)

```bash
POST http://localhost:8084/api/bookings
Content-Type: application/json

{
  "userId": 1,
  "flightId": 200,
  "hotelId": 55,
  "travelDate": "2025-01-10"
}
```

**Response:** 
```json
{
  "id": 1,
  "userId": 1,
  "flightId": 200,
  "hotelId": 55,
  "travelDate": "2025-01-10",
  "totalCost": 60000.00,
  "status": "PENDING",
  "bookingDate": "2024-12-11T10:30:00"
}
```

Note the booking `id` (e.g., 1)

### 5. Process Payment

```bash
POST http://localhost:8085/api/payments/process
Content-Type: application/json

{
  "bookingId": 1,
  "amount": 60000.00,
  "paymentMethod": "CREDIT_CARD"
}
```

**Response:**
```json
{
  "id": 1,
  "bookingId": 1,
  "amount": 60000.00,
  "paymentMethod": "CREDIT_CARD",
  "status": "COMPLETED",
  "transactionId": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
  "paymentDate": "2024-12-11T10:35:00"
}
```

### 6. Verify Booking Status

```bash
GET http://localhost:8084/api/bookings/1
```

**Response:** Status should now be "CONFIRMED"

### 7. Check Notifications

```bash
GET http://localhost:8086/api/notifications/user/1
```

## 📋 Additional API Endpoints

### User Service (8081)
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Flight Service (8082)
- `GET /api/flights` - Get all flights
- `GET /api/flights/{id}` - Get flight by ID
- `GET /api/flights/{id}/check-availability` - Check availability
- `PUT /api/flights/{id}` - Update flight
- `DELETE /api/flights/{id}` - Delete flight

### Hotel Service (8083)
- `GET /api/hotels` - Get all hotels
- `GET /api/hotels/{id}` - Get hotel by ID
- `GET /api/hotels/{id}/check-availability` - Check availability
- `PUT /api/hotels/{id}` - Update hotel
- `DELETE /api/hotels/{id}` - Delete hotel

### Booking Service (8084)
- `GET /api/bookings` - Get all bookings
- `GET /api/bookings/{id}` - Get booking by ID
- `GET /api/bookings/user/{userId}` - Get user's bookings
- `PUT /api/bookings/{id}/status?status=CONFIRMED` - Update status

### Payment Service (8085)
- `GET /api/payments` - Get all payments
- `GET /api/payments/{id}` - Get payment by ID
- `GET /api/payments/booking/{bookingId}` - Get payments by booking

### Notification Service (8086)
- `GET /api/notifications` - Get all notifications
- `GET /api/notifications/user/{userId}` - Get user notifications
- `GET /api/notifications/booking/{bookingId}` - Get booking notifications

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

## 📦 Project Structure

```
smart-travel-platform/
├── user-service/
│   ├── src/main/java/com/travel/user/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── entity/
│   │   ├── dto/
│   │   ├── repository/
│   │   └── UserServiceApplication.java
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
├── flight-service/ (similar structure)
├── hotel-service/ (similar structure)
├── booking-service/
│   ├── src/main/java/com/travel/booking/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── entity/
│   │   ├── dto/
│   │   ├── repository/
│   │   ├── client/ (Feign Clients)
│   │   ├── config/ (WebClient Config)
│   │   └── BookingServiceApplication.java
│   └── pom.xml
├── payment-service/ (similar structure with WebClient)
├── notification-service/ (similar structure)
└── README.md
```

## 🎯 Assignment Requirements Checklist

- ✅ 6 Microservices implemented
- ✅ Spring Boot 3+ with Java 17+
- ✅ Feign Client for Flight and Hotel communication
- ✅ WebClient for User, Notification, and Payment communication
- ✅ Controllers, Services, DTOs, Entities implemented
- ✅ Exception Handling implemented
- ✅ Each service on its own port
- ✅ MySQL database integration
- ✅ Complete booking flow implemented
- ✅ README with architecture diagram
- ✅ API documentation provided

## 📸 Testing Checklist

1. ✅ Create User
2. ✅ Create Flight
3. ✅ Create Hotel
4. ✅ Create Booking (tests WebClient + Feign)
5. ✅ Process Payment (tests WebClient to Booking)
6. ✅ Verify Booking Status Updated
7. ✅ Check Notifications Sent
8. ✅ Take screenshots of each step

## 👨‍💻 Author

Assignment for ITS 4243 - Microservices and Cloud Computing
University of Sri Jayewardenepura

## 📅 Submission Date

December 13, 2025
