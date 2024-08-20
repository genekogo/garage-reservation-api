-- Insert sample data into employee_types table
INSERT INTO employee_types (type_name) VALUES ('Mechanic');
INSERT INTO employee_types (type_name) VALUES ('Manager');

-- Insert sample data into employees table
-- Assigning employee types to employees
INSERT INTO employees (name, employee_type_id) VALUES ('Mechanic A', 1); -- Mechanic
INSERT INTO employees (name, employee_type_id) VALUES ('Mechanic B', 1); -- Mechanic
INSERT INTO employees (name, employee_type_id) VALUES ('Manager C', 2); -- Technician

-- Insert sample data into garage_working_hours table
INSERT INTO garage_working_hours (day_of_week, opening_time, closing_time) VALUES (1, '08:00:00', '18:00:00'); -- Monday
INSERT INTO garage_working_hours (day_of_week, opening_time, closing_time) VALUES (2, '08:00:00', '18:00:00'); -- Tuesday
INSERT INTO garage_working_hours (day_of_week, opening_time, closing_time) VALUES (3, '08:00:00', '18:00:00'); -- Wednesday
INSERT INTO garage_working_hours (day_of_week, opening_time, closing_time) VALUES (4, '08:00:00', '18:00:00'); -- Thursday
INSERT INTO garage_working_hours (day_of_week, opening_time, closing_time) VALUES (5, '08:00:00', '18:00:00'); -- Friday
INSERT INTO garage_working_hours (day_of_week, opening_time, closing_time) VALUES (6, '08:00:00', '14:00:00'); -- Saturday

-- Insert sample data into garage_closure_types table
INSERT INTO garage_closure_types (type_name) VALUES ('Public Holiday');
INSERT INTO garage_closure_types (type_name) VALUES ('Local Holiday');
INSERT INTO garage_closure_types (type_name) VALUES ('Maintenance');
INSERT INTO garage_closure_types (type_name) VALUES ('Special Event');
INSERT INTO garage_closure_types (type_name) VALUES ('Staff Training');
INSERT INTO garage_closure_types (type_name) VALUES ('Emergency');
INSERT INTO garage_closure_types (type_name) VALUES ('Company Policy');

-- Insert sample data into garage_non_working_days table
-- Assuming 'Public Holiday' has ID 1, 'Special Event' has ID 4, 'Staff Training' has ID 5, 'Emergency' has ID 6, 'Company Policy' has ID 7
INSERT INTO garage_non_working_days (closure_type_id, description, date) VALUES (1, 'New Year', '2024-01-01');
INSERT INTO garage_non_working_days (closure_type_id, description, date) VALUES (1, 'Good Friday', '2024-03-29');
INSERT INTO garage_non_working_days (closure_type_id, description, date) VALUES (1, 'Easter Sunday', '2024-03-31');
INSERT INTO garage_non_working_days (closure_type_id, description, date) VALUES (1, 'Easter Monday', '2024-04-01');
INSERT INTO garage_non_working_days (closure_type_id, description, date) VALUES (1, 'Kings Day', '2024-04-27');
INSERT INTO garage_non_working_days (closure_type_id, description, date) VALUES (1, 'Liberation Day', '2024-05-05');
INSERT INTO garage_non_working_days (closure_type_id, description, date) VALUES (1, 'Ascension Day', '2024-05-09');
INSERT INTO garage_non_working_days (closure_type_id, description, date) VALUES (1, 'Whit Monday', '2024-05-20');
INSERT INTO garage_non_working_days (closure_type_id, description, date) VALUES (1, 'Corpus Christi', '2024-05-30');
INSERT INTO garage_non_working_days (closure_type_id, description, date) VALUES (1, 'Christmas Day', '2024-12-25');
INSERT INTO garage_non_working_days (closure_type_id, description, date) VALUES (1, 'Boxing Day', '2024-12-26');
INSERT INTO garage_non_working_days (closure_type_id, description, date) VALUES (4, 'Annual Garage Sale', '2024-06-15');
INSERT INTO garage_non_working_days (closure_type_id, description, date) VALUES (5, 'Quarterly Training Session', '2024-09-10');
INSERT INTO garage_non_working_days (closure_type_id, description, date) VALUES (6, 'Flooding - Emergency Closure', '2024-08-25');
INSERT INTO garage_non_working_days (closure_type_id, description, date) VALUES (7, 'End-of-Year Closure', '2024-12-30');

-- Insert sample data into recurrence_patterns table
INSERT INTO recurrence_patterns (pattern_name) VALUES ('None');
INSERT INTO recurrence_patterns (pattern_name) VALUES ('Daily');
INSERT INTO recurrence_patterns (pattern_name) VALUES ('Weekly');
INSERT INTO recurrence_patterns (pattern_name) VALUES ('Monthly');

-- Insert sample data into employee_time_offs table
-- Records various types of time off for employees

-- Mechanic A is off every Tuesday and Friday in 2024-2025 (weekly recurrence)
INSERT INTO employee_time_offs (employee_id, start_date, start_time, end_date, end_time, recurrence_pattern_id, reason)
VALUES (1, '2024-01-01', NULL, '2025-12-31', NULL, 3, 'Regular Day Off - Tuesday');  -- Weekly Tuesday
INSERT INTO employee_time_offs (employee_id, start_date, start_time, end_date, end_time, recurrence_pattern_id, reason)
VALUES (1, '2024-01-01', NULL, '2025-12-31', NULL, 3, 'Regular Day Off - Friday');  -- Weekly Friday

-- Mechanic B is off every Saturday in 2024-2025 (weekly recurrence)
INSERT INTO employee_time_offs (employee_id, start_date, start_time, end_date, end_time, recurrence_pattern_id, reason)
VALUES (2, '2024-01-01', NULL, '2025-12-31', NULL, 3, 'Regular Day Off - Saturday');  -- Weekly Saturday

-- Additional time off records for Mechanics: emergency, illness, vacation, etc.
-- Mechanic A
INSERT INTO employee_time_offs (employee_id, start_date, start_time, end_date, end_time, recurrence_pattern_id, reason)
VALUES (1, '2024-08-22', NULL, '2024-08-22', NULL, 1, 'Family Emergency');  -- One day emergency
INSERT INTO employee_time_offs (employee_id, start_date, start_time, end_date, end_time, recurrence_pattern_id, reason)
VALUES (1, '2024-09-16', '08:00:00', '2024-09-16', '10:00:00', 1, 'Morning Doctor Appointment');  -- Partial day appointment
INSERT INTO employee_time_offs (employee_id, start_date, start_time, end_date, end_time, recurrence_pattern_id, reason)
VALUES (1, '2024-09-23', NULL, '2024-09-29', NULL, 1, 'Vacation');  -- Vacation for a week

-- Mechanic B
INSERT INTO employee_time_offs (employee_id, start_date, start_time, end_date, end_time, recurrence_pattern_id, reason)
VALUES (2, '2024-09-14', '11:00:00', '2024-09-14', '14:00:00', 1, 'Midday Appointment');  -- Partial day appointment
INSERT INTO employee_time_offs (employee_id, start_date, start_time, end_date, end_time, recurrence_pattern_id, reason)
VALUES (2, '2024-09-01', '14:00:00', '2024-10-01', '16:00:00', 3, 'Weekly Medical Appointment');  -- Extended weekly appointment
