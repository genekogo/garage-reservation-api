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

## Business Logic

### Overview

The Garage Reservation API manages the scheduling and booking of garage appointments. It handles the reservation of garage boxes and the allocation of mechanics for various operations. The primary business logic revolves around checking availability, booking appointments, and managing reservations.

### Key Components

#### 1. **Availability Checking**

The API determines whether mechanics and garage boxes are available for a given time slot on a specified date. This involves:

- **Fetching Mechanic Availability**: Retrieves the working hours of mechanics for a given day and checks if they are free during the desired time slot.
- **Slot Calculation**: Calculates available time slots based on mechanics' working hours and any pre-existing bookings.
- **Operation Constraints**: Ensures that the mechanics can perform the requested operations within their available slots.

#### 2. **Booking an Appointment**

When a customer requests to book an appointment, the API:

- **Validates the Booking Request**: Ensures the requested date and time are within acceptable limits (e.g., no booking too far in advance or too close to the current time).
- **Checks Mechanic and Garage Box Availability**: Verifies that a mechanic and a garage box are available for the requested time slot and operations.
- **Assigns Mechanics to Operations**: For each operation, finds an available mechanic, ensuring that the mechanic is free for the entire duration of the operation.
- **Creates and Saves the Appointment**: Generates a new appointment record, assigns the selected garage box and mechanics, and saves the appointment in the database.

#### 3. **Managing Available Slots**

- **Available Slots Endpoint**: Returns a list of available time slots for a given date and list of operation IDs. This list is dynamically calculated by considering both mechanics' working hours and any pre-existing appointments.
- **Cache Management**: Utilizes caching to optimize performance for frequently accessed available slots data. Cache entries are updated or evicted as necessary when appointments are booked to ensure up-to-date availability information.

#### 4. **Error Handling**

- **Validation Errors**: Handles cases where booking requests are invalid (e.g., invalid date, time conflicts).
- **Processing Errors**: Manages scenarios where no mechanics or garage boxes are available for the requested time slot or operations.
- **Custom Error Messages**: Provides detailed error messages to assist clients in understanding why a request may have failed.

### Business Rules

1. **Booking Constraints**:
    - **Max Advance Days**: A booking cannot be made more than a specified number of days in advance.
    - **Min Advance Minutes**: A booking must be made at least a specified number of minutes before the desired start time.

2. **Mechanic Availability**:
    - **Operation Duration**: Mechanics must be available for the entire duration of each operation.
    - **Overlap Checking**: Mechanic appointments are checked for overlap to avoid double-booking.

3. **Garage Box Allocation**:
    - **Single Box Allocation**: Each appointment is assigned a single garage box, which is selected based on availability.

4. **Slot Calculation**:
    - **Time Slot Division**: Available slots are divided into manageable intervals to match the duration of the operations requested.

### Summary

The Garage Reservation API provides a robust mechanism for managing garage appointments, ensuring that bookings are valid, mechanics and garage boxes are properly allocated, and available slots are efficiently managed and updated. The business logic is designed to maintain high accuracy and performance in scheduling and reservation processes.
