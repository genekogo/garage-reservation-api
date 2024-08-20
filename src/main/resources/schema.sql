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
