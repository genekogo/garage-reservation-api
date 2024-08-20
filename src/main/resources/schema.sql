-- Create the employee_types table
-- Defines various roles for employees in the garage
CREATE TABLE employee_types (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each employee type
    type_name VARCHAR(50) NOT NULL UNIQUE  -- Employee type ('Mechanic', 'Manager', ...)
);

-- Create the employees table
-- Stores information about each employee working at the garage
CREATE TABLE employees (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each employee
    name VARCHAR(100) NOT NULL,  -- Name of the employee
    employee_type_id INT NOT NULL,  -- Foreign key referencing employee_types table
    FOREIGN KEY (employee_type_id) REFERENCES employee_types(id)  -- Foreign key constraint
);

-- Create the garage_working_hours table
-- Defines the working hours for each day of the week
CREATE TABLE garage_working_hours (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each entry
    day_of_week INT NOT NULL,  -- Numeric representation of the day of the week (1=Monday, ..., 7=Sunday)
    opening_time TIME NOT NULL,  -- Garage opening time
    closing_time TIME NOT NULL  -- Garage closing time
);

-- Create the garage_closure_types table
-- Enumerates the types of closures or non-working days for the garage
CREATE TABLE garage_closure_types (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each closure type
    type_name VARCHAR(50) NOT NULL UNIQUE  -- Name of the closure type (e.g., 'Public Holiday', 'Maintenance')
);

-- Create the garage_non_working_days table
-- Records days when the garage is closed or non-working, along with the closure type
CREATE TABLE garage_non_working_days (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each non-working day record
    closure_type_id INT NOT NULL,  -- Foreign key referencing garage_closure_types table
    description VARCHAR(100) NOT NULL,  -- Description of the non-working day (e.g., 'New Year\'s Day')
    date DATE NOT NULL,  -- Date of the non-working day
    FOREIGN KEY (closure_type_id) REFERENCES garage_closure_types(id)  -- Foreign key constraint
);

-- Create the garage_operations table
-- Defines different types of operations the garage performs
CREATE TABLE garage_operations (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each operation
    name VARCHAR(100) NOT NULL,  -- Name of the operation
    duration_in_minutes INT NOT NULL  -- Duration of the operation in minutes
);

-- Create the recurrence_patterns table
-- Defines patterns for recurring time off or events
CREATE TABLE recurrence_patterns (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each recurrence pattern
    pattern_name VARCHAR(50) NOT NULL UNIQUE  -- Name of the recurrence pattern (e.g., 'None', 'Daily', 'Weekly')
);

-- Create the employee_time_offs table
-- Manages time off records for employees, including reasons and recurrence patterns
CREATE TABLE employee_time_offs (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each time off record
    employee_id INT NOT NULL,  -- Foreign key referencing employees table
    start_date DATE NOT NULL,  -- Start date of the time off
    start_time TIME,  -- Start time of the time off (optional)
    end_date DATE NOT NULL,  -- End date of the time off
    end_time TIME,  -- End time of the time off (optional)
    recurrence_pattern_id INT,  -- Foreign key referencing recurrence_patterns table (optional)
    reason VARCHAR(255) NOT NULL,  -- Reason for the time off (e.g., 'Vacation', 'Sick Leave')
    FOREIGN KEY (employee_id) REFERENCES employees(id),  -- Foreign key constraint
    FOREIGN KEY (recurrence_pattern_id) REFERENCES recurrence_patterns(id)  -- Foreign key constraint
);

-- Create the customers table
-- Stores information about each customer
CREATE TABLE customers (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each customer
    name VARCHAR(100) NOT NULL,  -- Customer's name
    phone_number VARCHAR(20) NOT NULL,  -- Customer's phone number (required)
    email VARCHAR(100)  -- Customer's email address (optional)
);

-- Create the appointments table
-- Manages reservation data for garage operations, including employee assignment and customer details
CREATE TABLE appointments (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each appointment
    employee_id INT NOT NULL,  -- Foreign key referencing employees table
    operation_id INT NOT NULL,  -- Foreign key referencing garage_operations table
    customer_id INT NOT NULL,  -- Foreign key referencing customers table
    appointment_date DATE NOT NULL,  -- Date of the appointment
    start_time TIME NOT NULL,  -- Start time of the appointment
    end_time TIME NOT NULL,  -- End time of the appointment
    FOREIGN KEY (employee_id) REFERENCES employees(id),  -- Foreign key constraint
    FOREIGN KEY (operation_id) REFERENCES garage_operations(id),  -- Foreign key constraint
    FOREIGN KEY (customer_id) REFERENCES customers(id)  -- Foreign key constraint
);
