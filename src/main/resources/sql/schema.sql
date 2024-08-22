-- **Employee Types**

CREATE TABLE employee_types (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each employee type
    name VARCHAR(255) NOT NULL  -- Name of the employee type (e.g., "Mechanic", "Manager")
);

-- **Employees**

CREATE TABLE employees (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each employee
    full_name VARCHAR(255) NOT NULL,  -- Full name of the employee
    employee_type_id INT NOT NULL,  -- Foreign key referencing employee_types table
    FOREIGN KEY (employee_type_id) REFERENCES employee_types(id)  -- Foreign key constraint
);

-- **Garage Operations**

CREATE TABLE garage_operations (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each operation
    name VARCHAR(255) NOT NULL,  -- Name of the operation (e.g., "General Check")
    duration_in_minutes INT NOT NULL  -- Duration of the operation in minutes
);

-- **Employee Working Hours**

CREATE TABLE employee_working_hours (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each working hours entry
    employee_id INT NOT NULL,  -- Foreign key referencing employees table
    day_of_week ENUM('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY') NOT NULL,  -- Day of the week
    start_time TIME NOT NULL,  -- Starting time of the employee
    end_time TIME NOT NULL,  -- Ending time of the employee
    FOREIGN KEY (employee_id) REFERENCES employees(id)  -- Foreign key constraint
);

-- **Garage Boxes**

CREATE TABLE garage_boxes (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each garage box
    name VARCHAR(255) NOT NULL  -- Name of the box (e.g., "Box 1")
);

-- **Garage Closure Types**

CREATE TABLE garage_closure_types (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each closure type
    name VARCHAR(255) NOT NULL  -- Name of the closure type (e.g., "Public Holiday")
);

-- **Garage Closures**

CREATE TABLE garage_closures (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each closure entry
    closure_date DATE NOT NULL,  -- Date of the closure
    closure_type_id INT NOT NULL,  -- Foreign key referencing closure types table
    description VARCHAR(255),  -- Description of the closure
    FOREIGN KEY (closure_type_id) REFERENCES garage_closure_types(id)  -- Foreign key constraint
);

-- **Customers**

CREATE TABLE customers (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each customer
    full_name VARCHAR(255) NOT NULL,  -- Full name of the customer
    phone_number VARCHAR(20) NOT NULL,  -- Phone number of the customer
    email VARCHAR(255)  -- Email address of the customer (optional)
);

-- **Garage Appointments**

CREATE TABLE garage_appointments (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each appointment
    customer_id INT NOT NULL,  -- Foreign key referencing customers table
    garage_box_id INT NOT NULL,  -- Foreign key referencing garage_boxes table
    `date` DATE NOT NULL,  -- Date of the appointment
    start_time TIME NOT NULL,  -- Start time of the appointment
    end_time TIME NOT NULL,  -- End time of the appointment
    FOREIGN KEY (customer_id) REFERENCES customers(id),  -- Foreign key constraint
    FOREIGN KEY (garage_box_id) REFERENCES garage_boxes(id)  -- Foreign key constraint
);

-- **Garage Appointment Operations Join Table**

CREATE TABLE garage_appointment_operations (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- Unique identifier for each operation record
    appointment_id INT NOT NULL,  -- Foreign key referencing the garage_appointments table
    operation_id INT NOT NULL,  -- Foreign key referencing the garage_operations table
    employee_id INT NOT NULL,  -- Foreign key referencing the employees table
    FOREIGN KEY (appointment_id) REFERENCES garage_appointments(id),  -- Foreign key constraint
    FOREIGN KEY (operation_id) REFERENCES garage_operations(id),  -- Foreign key constraint
    FOREIGN KEY (employee_id) REFERENCES employees(id)  -- Foreign key constraint
);

