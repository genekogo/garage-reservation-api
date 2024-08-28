# Garage Reservation System

## Overview

The Garage Reservation System is a comprehensive application designed to manage garage appointments, operations, and
mechanic availability. It offers features for customers to book appointments, check available slots, and efficiently
schedule mechanics based on their working hours and existing appointments.

## Features

- **Appointment Booking**: Customers can book appointments with available mechanics.
- **Availability Checking**: Users can find available time slots based on mechanic availability and operational
  constraints.
- **Cache Management**: Efficiently caches available slots and evicts outdated cache entries when appointments are
  booked.
- **Error Handling**: Includes robust error handling for validation and processing issues.

## Setup

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Installation

1. **Clone the Repository**

    ```bash
    git clone https://github.com/your-repository/garage-reservation.git
    cd garage-reservation
    ```
2. **Build the Project**

    ```bash
    mvn clean install
    ```

3. **Run the Application**

    ```bash
    mvn spring-boot:run
    ```

## H2 Console

The H2 Console provides a web-based interface to interact with the H2 database. You can use it to run SQL queries and manage your database schema.

### Accessing the H2 Console

- **URL:** [http://localhost:8080/garage/h2-console](http://localhost:8080/garage/h2-console)
- **Username:** `sa`
- **Password:** `password`

## Swagger UI

Swagger UI provides an interactive API documentation interface for exploring and testing your API endpoints.

### Accessing Swagger UI

- **URL:** [http://localhost:8080/garage/api/v1/swagger-ui/index.html](http://localhost:8080/garage/api/v1/swagger-ui/index.html)

## API Request Examples

### Get Available Slots

Retrieve available slots for a specified date and list of operation IDs.

#### Request

**Method:** `GET`  
**URL:** [http://localhost:8080/garage/api/v1/reservations/availableSlots](http://localhost:8080/garage/api/v1/reservations/availableSlots)  
**Query Parameters:**

- `date` (required): The date for which to find available slots (format: `YYYY-MM-DD`).
- `operationIds` (required): A comma-separated list of operation IDs to check for availability.

#### Example

GET [http://localhost:8080/garage/api/v1/reservations/availableSlots?date=2024-08-30&operationIds=1,2,3](http://localhost:8080/garage/api/v1/reservations/availableSlots?date=2024-08-30&operationIds=1,2,3)


### Book Appointment

Create a new appointment with the specified details.

#### Request

**Method:** `POST`  
**URL:** [http://localhost:8080/garage/api/v1/reservations/book](http://localhost:8080/garage/api/v1/reservations/book)  
**Content-Type:** `application/json`

#### Request Body

```json
{
    "customerId": 1,
    "date": "2024-08-30",
    "startTime": "08:00:00",
    "endTime": "12:30:00",
    "operationIds": [1, 2, 3]
}
```

## Postman Collection

A Postman collection is provided to help you test the Garage Reservation API easily. The collection includes pre-configured requests for various endpoints of the API.

### Location

The Postman collection file is located in the `postman` folder within the repository.

### File Details

- **File Name:** `Garage Reservation API.postman_collection.json`
- **Path:** `postman/Garage Reservation API.postman_collection.json`

### How to Import

To use the Postman collection:

1. **Open Postman.**
2. **Click on the "Import" button** in the top-left corner of the Postman interface.
3. **Select the "Upload Files" tab** and click "Choose Files".
4. **Navigate to the `postman` folder** in the repository and select the `Garage Reservation API.postman_collection.json` file.
5. **Click "Open"** to import the collection.

Once imported, you can view and run the various API requests defined in the collection.

Using this collection will help you quickly test and interact with the Garage Reservation API without having to manually configure requests.

## Note on JUnit Tests

Please be aware that JUnit tests for this project have been skipped.