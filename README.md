# Employee Burnout Prediction System

## Overview

The Employee Burnout Prediction System is a Java-based application developed using Object-Oriented Programming (OOP), Data Structures and Algorithms (DSA), JDBC, and MySQL. The system helps organizations identify employees who are at risk of burnout by analyzing factors such as working hours, stress levels, work-life balance, support level, and mental fatigue.

This project demonstrates the implementation of advanced Java concepts, database connectivity, data structures, and basic machine learning techniques for workforce analytics.

---

## Features

- Employee Registration and Management
- Burnout Risk Prediction
- Employee Search by ID
- Employee Listing
- Top Burnout Risk Analysis using Max Heap
- Burnout Range Search using Binary Search Tree (BST)
- Gradient Descent Based Model Training
- MySQL Database Integration
- Input Validation and Exception Handling
- Report Generation
- Workforce Analytics

---

## Technologies Used

- Java
- JDBC
- MySQL
- Object-Oriented Programming (OOP)
- Binary Search Tree (BST)
- Max Heap
- Gradient Descent Algorithm
- Exception Handling
- File Handling

---

## Data Structures Implemented

### Binary Search Tree (BST)
Used for:
- Efficient employee storage
- Employee searching
- Burnout range queries

### Max Heap
Used for:
- Identifying top burnout-risk employees
- Priority-based employee ranking

---

## Machine Learning Concept

The system uses a Gradient Descent-based approach to optimize burnout prediction weights and improve prediction accuracy over time.

Factors considered:
- Working Hours
- Stress Level
- Work-Life Balance
- Support Level
- Mental Fatigue

---

## Project Structure

```text
EmployeeBurnoutSystem
│
├── EmployeeBurnoutSystem.java
├── README.md
├── INPUT_OUTPUT.txt
├── .gitignore
└── lib
    └── mysql-connector-j-9.7.0.jar
```

---

## Prerequisites

Before running the project, ensure that:

- Java JDK 17 or later is installed
- MySQL Server is installed and running
- MySQL Connector/J is available

---

## Database Setup

Create a database:

```sql
CREATE DATABASE burnout_db;
```

Update the database credentials inside the source code if necessary:

```java
private static final String URL = "jdbc:mysql://localhost:3306/burnout_db";
private static final String USER = "root";
private static final String PASS = "your_password";
```

---

## Compilation

Windows:

```cmd
javac -cp ".;lib\mysql-connector-j-9.7.0\mysql-connector-j-9.7.0.jar" EmployeeBurnoutSystem.java
```

---

## Execution

Windows:

```cmd
java -cp ".;lib\mysql-connector-j-9.7.0\mysql-connector-j-9.7.0.jar" EmployeeBurnoutSystem
```

---

## Sample Input

```text
Employee ID: 201
Name: Rohith Kumar
Email: rohith@company.com
Department: IT
Working Hours: 12
Stress Level: 10
Work-Life Balance: 1
Support Level: 1
Mental Fatigue: 10
```

---

## Sample Output

```text
Employee Registered Successfully

Burnout Index: 0.92
Risk Level: HIGH
```

---

## Learning Outcomes

This project demonstrates:

- Object-Oriented Programming
- JDBC Database Connectivity
- Data Structures and Algorithms
- Exception Handling
- File Handling
- Workforce Analytics
- Problem Solving
- Software Development Best Practices

---

## Future Enhancements

- GUI using JavaFX or Swing
- Employee Performance Dashboard
- Email Alert System
- Cloud Database Integration
- REST API Integration
- Advanced Machine Learning Models

---

## Author

** S Rohith **

---

## License

This project is developed for educational and portfolio purposes.
