-- **Employee Types**

INSERT INTO employee_types (name) VALUES ('Mechanic');
INSERT INTO employee_types (name) VALUES ('Manager');

-- **Employees**

INSERT INTO employees (full_name, employee_type_id) VALUES ('Mechanic A', (SELECT id FROM employee_types WHERE name = 'Mechanic'));
INSERT INTO employees (full_name, employee_type_id) VALUES ('Mechanic B', (SELECT id FROM employee_types WHERE name = 'Mechanic'));
INSERT INTO employees (full_name, employee_type_id) VALUES ('Manager A', (SELECT id FROM employee_types WHERE name = 'Manager'));

-- **Garage Operations**

INSERT INTO garage_operations (name, duration_in_minutes) VALUES ('General Check', 180);
INSERT INTO garage_operations (name, duration_in_minutes) VALUES ('Tire Replacement', 60);
INSERT INTO garage_operations (name, duration_in_minutes) VALUES ('Broken Lamp Change', 30);

-- **Employee Working Hours**

-- Mechanic A has days off on Tuesday and Friday
INSERT INTO employee_working_hours (employee_id, day_of_week, start_time, end_time) VALUES
((SELECT id FROM employees WHERE full_name = 'Mechanic A'), 'MONDAY', '08:00:00', '17:00:00'),
((SELECT id FROM employees WHERE full_name = 'Mechanic A'), 'WEDNESDAY', '08:00:00', '17:00:00'),
((SELECT id FROM employees WHERE full_name = 'Mechanic A'), 'THURSDAY', '08:00:00', '17:00:00'),
((SELECT id FROM employees WHERE full_name = 'Mechanic A'), 'SATURDAY', '08:00:00', '17:00:00');

-- Mechanic B has day off on Saturday
INSERT INTO employee_working_hours (employee_id, day_of_week, start_time, end_time) VALUES
((SELECT id FROM employees WHERE full_name = 'Mechanic B'), 'MONDAY', '08:00:00', '17:00:00'),
((SELECT id FROM employees WHERE full_name = 'Mechanic B'), 'TUESDAY', '08:00:00', '17:00:00'),
((SELECT id FROM employees WHERE full_name = 'Mechanic B'), 'WEDNESDAY', '08:00:00', '17:00:00'),
((SELECT id FROM employees WHERE full_name = 'Mechanic B'), 'THURSDAY', '08:00:00', '17:00:00'),
((SELECT id FROM employees WHERE full_name = 'Mechanic B'), 'FRIDAY', '08:00:00', '17:00:00');

-- **Garage Boxes**

INSERT INTO garage_boxes (name) VALUES ('Box 1');
INSERT INTO garage_boxes (name) VALUES ('Box 2');

-- **Garage Closure Types**

INSERT INTO garage_closure_types (name) VALUES ('Public Holiday');
INSERT INTO garage_closure_types (name) VALUES ('Local Holiday');
INSERT INTO garage_closure_types (name) VALUES ('Maintenance');

-- **Garage Closures**

-- Adding holidays in Netherlands for 2024
INSERT INTO garage_closures (closure_date, closure_type_id, description) VALUES
('2024-01-01', (SELECT id FROM garage_closure_types WHERE name = 'Public Holiday'), 'New Years Day'),
('2024-04-27', (SELECT id FROM garage_closure_types WHERE name = 'Public Holiday'), 'Kings Day'),
('2024-05-05', (SELECT id FROM garage_closure_types WHERE name = 'Public Holiday'), 'Liberation Day'),
('2024-12-25', (SELECT id FROM garage_closure_types WHERE name = 'Public Holiday'), 'Christmas Day'),
('2024-12-26', (SELECT id FROM garage_closure_types WHERE name = 'Public Holiday'), 'Boxing Day');

-- **Customers**

INSERT INTO customers (full_name, phone_number, email) VALUES
('John Doe', '123-456-7890', 'john.doe@example.com'),
('Jane Smith', '098-765-4321', NULL),  -- Email is optional
('Emily Davis', '555-123-4567', 'emily.davis@example.com'),
('Michael Brown', '555-987-6543', NULL),  -- Email is optional
('Linda Wilson', '555-678-1234', 'linda.wilson@example.com');
