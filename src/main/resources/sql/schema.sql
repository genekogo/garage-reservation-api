-- **Employee Types**
CREATE TABLE employee_types (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each employee type
    name VARCHAR(255) NOT NULL
);

-- Enforce uniqueness on the employee type name
CREATE UNIQUE INDEX idx_employee_types_name ON employee_types(name);

-- **Employees**
CREATE TABLE employees (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each employee
    full_name VARCHAR(255) NOT NULL,
    employee_type_id INT NOT NULL,  -- Foreign key referencing employee_types table
    FOREIGN KEY (employee_type_id) REFERENCES employee_types(id)
);

-- Index on employee_type_id for faster lookups
CREATE INDEX idx_employee_type_id ON employees(employee_type_id);

-- **Garage Operations**
CREATE TABLE garage_operations (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each operation
    name VARCHAR(255) NOT NULL,
    duration_in_minutes INT NOT NULL
);

-- Index on name for quicker searches by operation name
CREATE INDEX idx_garage_operations_name ON garage_operations(name);

-- **Employee Working Hours**
CREATE TABLE employee_working_hours (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each working hours entry
    employee_id INT NOT NULL,  -- Foreign key referencing employees table
    day_of_week ENUM('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

-- Composite index for queries filtering by employee and day of week
CREATE INDEX idx_employee_id_day_of_week ON employee_working_hours(employee_id, day_of_week);

-- Index on day_of_week for quicker day-based lookups
CREATE INDEX idx_day_of_week ON employee_working_hours(day_of_week);

-- **Garage Boxes**
CREATE TABLE garage_boxes (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each garage box
    name VARCHAR(255) NOT NULL
);

-- Enforce uniqueness on garage box name
CREATE UNIQUE INDEX idx_garage_boxes_name ON garage_boxes(name);

-- **Garage Closure Types**
CREATE TABLE garage_closure_types (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each closure type
    name VARCHAR(255) NOT NULL
);

-- Enforce uniqueness on closure type name
CREATE UNIQUE INDEX idx_garage_closure_types_name ON garage_closure_types(name);

-- **Garage Closures**
CREATE TABLE garage_closures (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each closure entry
    closure_date DATE NOT NULL,
    closure_type_id INT NOT NULL,  -- Foreign key referencing closure types table
    description VARCHAR(255),
    FOREIGN KEY (closure_type_id) REFERENCES garage_closure_types(id)
);

-- Index on closure_date for faster searches by date
CREATE INDEX idx_closure_date ON garage_closures(closure_date);

-- Index on closure_type_id for quicker closure type lookups
CREATE INDEX idx_closure_type_id ON garage_closures(closure_type_id);

-- **Customers**
CREATE TABLE customers (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each customer
    full_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(255)
);

-- Index on phone_number for faster lookups by phone
CREATE INDEX idx_phone_number ON customers(phone_number);

-- Index on email for faster lookups by email
CREATE INDEX idx_email ON customers(email);

-- **Garage Appointments**
CREATE TABLE garage_appointments (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each appointment
    customer_id INT NOT NULL,  -- Foreign key referencing customers table
    garage_box_id INT NOT NULL,  -- Foreign key referencing garage_boxes table
    `date` DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (garage_box_id) REFERENCES garage_boxes(id)
);

-- Index on customer_id for quicker customer lookups
CREATE INDEX idx_customer_id ON garage_appointments(customer_id);

-- Index on garage_box_id for faster garage box lookups
CREATE INDEX idx_garage_box_id ON garage_appointments(garage_box_id);

-- Composite index for queries filtering by date and time range
CREATE INDEX idx_date_start_time_end_time ON garage_appointments(`date`, start_time, end_time);

-- **Garage Appointment Operations Join Table**
CREATE TABLE garage_appointment_operations (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each operation record
    appointment_id INT NOT NULL,  -- Foreign key referencing the garage_appointments table
    operation_id INT NOT NULL,  -- Foreign key referencing the garage_operations table
    employee_id INT NOT NULL,  -- Foreign key referencing the employees table
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    FOREIGN KEY (appointment_id) REFERENCES garage_appointments(id),
    FOREIGN KEY (operation_id) REFERENCES garage_operations(id),
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

-- Index on appointment_id for faster lookups
CREATE INDEX idx_appointment_id ON garage_appointment_operations(appointment_id);

-- Index on employee_id for quicker employee lookups
CREATE INDEX idx_employee_id ON garage_appointment_operations(employee_id);

-- Index on operation_id for quicker operation lookups
CREATE INDEX idx_operation_id ON garage_appointment_operations(operation_id);

-- Composite index for queries filtering by employee, appointment, and time range
CREATE INDEX idx_employee_appointment_date_time ON garage_appointment_operations(employee_id, appointment_id, start_time, end_time);
