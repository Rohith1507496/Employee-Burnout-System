import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Main Application Class: Employee Burnout Prediction System
 * Demonstrates intermediate Java concepts, custom DSA (BST, Max-Heap, Gradient Descent),
 * OOP design, JDBC Connectivity, Validation, and File I/O.
 */
public class EmployeeBurnoutSystem {

    // Global Managers
    private static DatabaseManager dbManager;
    private static EmployeeBST employeeBST = new EmployeeBST();
    private static final Scanner scanner = new Scanner(System.in);

    // ==========================================
    // 1. CUSTOM VALIDATION EXCEPTION
    // ==========================================
    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    // ==========================================
    // 2. OOP ABSTRACTIONS & INHERITANCE
    // ==========================================
    public interface Predictable {
        double predictBurnout();
    }

    public static abstract class Person {
        protected int id;
        protected String name;
        protected String email;

        public Person(int id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        // Encapsulation: Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public abstract void displayDetails();
    }

    public static class Employee extends Person implements Predictable {
        private String department;
        private double workHoursPerDay;
        private int stressLevel;      // Scale: 1 - 10
        private int workLifeBalance;  // Scale: 1 - 5
        private int peerSupport;      // Scale: 1 - 5
        private double mentalFatigue; // Scale: 0.0 - 10.0
        private double burnoutIndex;  // Predicted Value: 0.0 - 1.0

        public Employee(int id, String name, String email, String department,
                        double workHoursPerDay, int stressLevel, int workLifeBalance,
                        int peerSupport, double mentalFatigue) {
            super(id, name, email);
            this.department = department;
            this.workHoursPerDay = workHoursPerDay;
            this.stressLevel = stressLevel;
            this.workLifeBalance = workLifeBalance;
            this.peerSupport = peerSupport;
            this.mentalFatigue = mentalFatigue;
            this.burnoutIndex = 0.0;
        }

        // Getters and Setters
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public double getWorkHoursPerDay() { return workHoursPerDay; }
        public void setWorkHoursPerDay(double workHoursPerDay) { this.workHoursPerDay = workHoursPerDay; }
        public int getStressLevel() { return stressLevel; }
        public void setStressLevel(int stressLevel) { this.stressLevel = stressLevel; }
        public int getWorkLifeBalance() { return workLifeBalance; }
        public void setWorkLifeBalance(int workLifeBalance) { this.workLifeBalance = workLifeBalance; }
        public int getPeerSupport() { return peerSupport; }
        public void setPeerSupport(int peerSupport) { this.peerSupport = peerSupport; }
        public double getMentalFatigue() { return mentalFatigue; }
        public void setMentalFatigue(double mentalFatigue) { this.mentalFatigue = mentalFatigue; }
        public double getBurnoutIndex() { return burnoutIndex; }
        public void setBurnoutIndex(double burnoutIndex) { this.burnoutIndex = burnoutIndex; }

        @Override
        public double predictBurnout() {
            this.burnoutIndex = BurnoutPredictor.predict(this);
            return this.burnoutIndex;
        }

        @Override
        public void displayDetails() {
            System.out.printf("| ID: %-4d | Name: %-15s | Dept: %-12s | Hours/Day: %-4.1f | Stress: %-2d | Fatigue: %-4.1f | Score: %-6.4f |\n",
                    id, name, department, workHoursPerDay, stressLevel, mentalFatigue, burnoutIndex);
        }
    }

    // ==========================================
    // 3. VALIDATION MODULE
    // ==========================================
    public static class InputValidator {
        private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

        public static void validateId(int id) throws ValidationException {
            if (id <= 0) {
                throw new ValidationException("Employee ID must be a positive integer.");
            }
        }

        public static void validateName(String name) throws ValidationException {
            if (name == null || name.trim().isEmpty()) {
                throw new ValidationException("Employee Name cannot be empty.");
            }
            if (name.length() < 2) {
                throw new ValidationException("Employee Name must be at least 2 characters.");
            }
        }

        public static void validateEmail(String email) throws ValidationException {
            if (email == null || email.trim().isEmpty()) {
                throw new ValidationException("Email cannot be empty.");
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new ValidationException("Invalid Email format (e.g. email@company.com).");
            }
        }

        public static void validateDepartment(String dept) throws ValidationException {
            if (dept == null || dept.trim().isEmpty()) {
                throw new ValidationException("Department cannot be empty.");
            }
        }

        public static void validateWorkHours(double hours) throws ValidationException {
            if (hours < 0.0 || hours > 24.0) {
                throw new ValidationException("Work Hours must be between 0.0 and 24.0 hours per day.");
            }
        }

        public static void validateStressLevel(int level) throws ValidationException {
            if (level < 1 || level > 10) {
                throw new ValidationException("Stress Level must be between 1 (Low) and 10 (Extreme).");
            }
        }

        public static void validateWorkLifeBalance(int level) throws ValidationException {
            if (level < 1 || level > 5) {
                throw new ValidationException("Work-Life Balance must be between 1 (Poor) and 5 (Excellent).");
            }
        }

        public static void validatePeerSupport(int level) throws ValidationException {
            if (level < 1 || level > 5) {
                throw new ValidationException("Peer Support must be between 1 (None) and 5 (Strong).");
            }
        }

        public static void validateMentalFatigue(double score) throws ValidationException {
            if (score < 0.0 || score > 10.0) {
                throw new ValidationException("Mental Fatigue must be between 0.0 (None) and 10.0 (Extreme).");
            }
        }
    }

    // ==========================================
    // 4. DATABASE MODULE (MySQL JDBC + Mock Fallback)
    // ==========================================
    public static class DatabaseManager {
        private static final String URL = "jdbc:mysql://localhost:3306/burnout_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        private static final String USER = "root";
        private static final String PASS = "root"; // Default local password
        
        private Connection conn = null;
        private boolean isMockMode = false;
        private final Map<Integer, Employee> mockDb = new LinkedHashMap<>();

        public DatabaseManager() {
            try {
                // Force driver loading
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("[Database Info] Connected to MySQL successfully.");
            } catch (ClassNotFoundException e) {
                System.out.println("[Database Warning] MySQL JDBC Driver not found. Falling back to In-Memory Mock Database.");
                isMockMode = true;
            } catch (SQLException e) {
                System.out.println("[Database Warning] Could not connect to MySQL server on localhost:3306.");
                System.out.println("                 Switching to In-Memory Mock Database mode.");
                isMockMode = true;
            }
        }

        public boolean isMockMode() {
            return isMockMode;
        }

        public void initializeDatabase() {
            if (isMockMode) {
                System.out.println("[Database Info] Seeded sample values in In-Memory Database.");
                seedMockData();
                return;
            }
            
            String createTableSQL = "CREATE TABLE IF NOT EXISTS employees (" +
                    "id INT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL, " +
                    "department VARCHAR(100) NOT NULL, " +
                    "work_hours DOUBLE NOT NULL, " +
                    "stress_level INT NOT NULL, " +
                    "work_life_balance INT NOT NULL, " +
                    "peer_support INT NOT NULL, " +
                    "mental_fatigue DOUBLE NOT NULL, " +
                    "burnout_index DOUBLE NOT NULL" +
                    ")";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
                System.out.println("[Database Info] Table 'employees' verified/created in MySQL.");
                
                // Seed database if empty
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM employees");
                if (rs.next() && rs.getInt(1) == 0) {
                    seedMySQLData();
                }
            } catch (SQLException e) {
                System.err.println("[Database Error] SQL initialization failed: " + e.getMessage());
                System.out.println("[Database Warning] Falling back to In-Memory Mock mode.");
                isMockMode = true;
                seedMockData();
            }
        }

        private void seedMockData() {
            mockDb.put(101, new Employee(101, "Alice Smith", "alice@company.com", "Engineering", 9.5, 8, 2, 2, 7.5));
            mockDb.put(102, new Employee(102, "Bob Jones", "bob@company.com", "Marketing", 7.5, 4, 4, 3, 3.2));
            mockDb.put(103, new Employee(103, "Charlie Brown", "charlie@company.com", "Sales", 10.0, 9, 1, 1, 9.0));
            mockDb.put(104, new Employee(104, "Diana Prince", "diana@company.com", "HR", 8.0, 3, 5, 5, 2.0));
            mockDb.put(105, new Employee(105, "Ethan Hunt", "ethan@company.com", "Operations", 11.5, 9, 2, 2, 8.8));
            
            for (Employee emp : mockDb.values()) {
                emp.predictBurnout();
            }
        }

        private void seedMySQLData() {
            List<Employee> seedList = new ArrayList<>();
            seedList.add(new Employee(101, "Alice Smith", "alice@company.com", "Engineering", 9.5, 8, 2, 2, 7.5));
            seedList.add(new Employee(102, "Bob Jones", "bob@company.com", "Marketing", 7.5, 4, 4, 3, 3.2));
            seedList.add(new Employee(103, "Charlie Brown", "charlie@company.com", "Sales", 10.0, 9, 1, 1, 9.0));
            seedList.add(new Employee(104, "Diana Prince", "diana@company.com", "HR", 8.0, 3, 5, 5, 2.0));
            seedList.add(new Employee(105, "Ethan Hunt", "ethan@company.com", "Operations", 11.5, 9, 2, 2, 8.8));

            for (Employee emp : seedList) {
                emp.predictBurnout();
                saveEmployee(emp);
            }
        }

        public void saveEmployee(Employee emp) {
            if (isMockMode) {
                mockDb.put(emp.getId(), emp);
                return;
            }

            String checkSQL = "SELECT COUNT(*) FROM employees WHERE id = ?";
            String insertSQL = "INSERT INTO employees VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String updateSQL = "UPDATE employees SET name=?, email=?, department=?, work_hours=?, stress_level=?, work_life_balance=?, peer_support=?, mental_fatigue=?, burnout_index=? WHERE id=?";

            try {
                boolean exists = false;
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {
                    checkStmt.setInt(1, emp.getId());
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            exists = true;
                        }
                    }
                }

                if (exists) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                        updateStmt.setString(1, emp.getName());
                        updateStmt.setString(2, emp.getEmail());
                        updateStmt.setString(3, emp.getDepartment());
                        updateStmt.setDouble(4, emp.getWorkHoursPerDay());
                        updateStmt.setInt(5, emp.getStressLevel());
                        updateStmt.setInt(6, emp.getWorkLifeBalance());
                        updateStmt.setInt(7, emp.getPeerSupport());
                        updateStmt.setDouble(8, emp.getMentalFatigue());
                        updateStmt.setDouble(9, emp.getBurnoutIndex());
                        updateStmt.setInt(10, emp.getId());
                        updateStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                        insertStmt.setInt(1, emp.getId());
                        insertStmt.setString(2, emp.getName());
                        insertStmt.setString(3, emp.getEmail());
                        insertStmt.setString(4, emp.getDepartment());
                        insertStmt.setDouble(5, emp.getWorkHoursPerDay());
                        insertStmt.setInt(6, emp.getStressLevel());
                        insertStmt.setInt(7, emp.getWorkLifeBalance());
                        insertStmt.setInt(8, emp.getPeerSupport());
                        insertStmt.setDouble(9, emp.getMentalFatigue());
                        insertStmt.setDouble(10, emp.getBurnoutIndex());
                        insertStmt.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                System.err.println("[Database Error] SQL Write error: " + e.getMessage());
            }
        }

        public Employee getEmployeeById(int id) {
            if (isMockMode) {
                return mockDb.get(id);
            }

            String selectSQL = "SELECT * FROM employees WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(selectSQL)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Employee emp = new Employee(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getString("department"),
                                rs.getDouble("work_hours"),
                                rs.getInt("stress_level"),
                                rs.getInt("work_life_balance"),
                                rs.getInt("peer_support"),
                                rs.getDouble("mental_fatigue")
                        );
                        emp.setBurnoutIndex(rs.getDouble("burnout_index"));
                        return emp;
                    }
                }
            } catch (SQLException e) {
                System.err.println("[Database Error] SQL Fetch error: " + e.getMessage());
            }
            return null;
        }

        public List<Employee> getAllEmployees() {
            List<Employee> list = new ArrayList<>();
            if (isMockMode) {
                list.addAll(mockDb.values());
                return list;
            }

            String selectAllSQL = "SELECT * FROM employees";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectAllSQL)) {
                while (rs.next()) {
                    Employee emp = new Employee(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("department"),
                            rs.getDouble("work_hours"),
                            rs.getInt("stress_level"),
                            rs.getInt("work_life_balance"),
                            rs.getInt("peer_support"),
                            rs.getDouble("mental_fatigue")
                    );
                    emp.setBurnoutIndex(rs.getDouble("burnout_index"));
                    list.add(emp);
                }
            } catch (SQLException e) {
                System.err.println("[Database Error] SQL Read error: " + e.getMessage());
            }
            return list;
        }
    }

    // ==========================================
    // 5. BURNOUT PREDICTOR (Regression & Gradient Descent Model)
    // ==========================================
    public static class BurnoutPredictor {
        // Model Parameters (Weights & Bias)
        private static double wWorkHours = 0.22;
        private static double wStress = 0.28;
        private static double wFatigue = 0.35;
        private static double wWorkLife = -0.10;
        private static double wPeerSupport = -0.05;
        private static double bias = 0.05;

        public static double predict(Employee emp) {
            // Normalize all features into [0, 1] scale
            double x1 = Math.min(emp.getWorkHoursPerDay() / 12.0, 2.0); // Work hours relative to a standard 12-hour day
            double x2 = (emp.getStressLevel() - 1) / 9.0;               // Stress score scaled
            double x3 = emp.getMentalFatigue() / 10.0;                  // Fatigue score scaled
            double x4 = (emp.getWorkLifeBalance() - 1) / 4.0;           // Work-life balance scaled
            double x5 = (emp.getPeerSupport() - 1) / 4.0;               // Peer support scaled

            // Linear combination
            double score = (x1 * wWorkHours) + (x2 * wStress) + (x3 * wFatigue) + (x4 * wWorkLife) + (x5 * wPeerSupport) + bias;

            // Clamping the predicted index between [0.0, 1.0]
            return Math.max(0.0, Math.min(1.0, score));
        }

        /**
         * Real-world Optimization: Runs Gradient Descent parameter training on database dataset
         * to adjust model weights to closely fit mock HR observations.
         */
        public static void trainModel(List<Employee> dataset) {
            if (dataset.size() < 3) {
                System.out.println("[Model Warning] Training requires at least 3 employees. Training aborted.");
                return;
            }

            System.out.println("\n[Model] Initializing Gradient Descent Parameter Optimization...");
            double learningRate = 0.05;
            int epochs = 100;

            for (int epoch = 1; epoch <= epochs; epoch++) {
                double loss = 0.0;
                double dw1 = 0, dw2 = 0, dw3 = 0, dw4 = 0, dw5 = 0, db = 0;

                for (Employee emp : dataset) {
                    double x1 = Math.min(emp.getWorkHoursPerDay() / 12.0, 2.0);
                    double x2 = (emp.getStressLevel() - 1) / 9.0;
                    double x3 = emp.getMentalFatigue() / 10.0;
                    double x4 = (emp.getWorkLifeBalance() - 1) / 4.0;
                    double x5 = (emp.getPeerSupport() - 1) / 4.0;

                    // Simulated empirical supervisor evaluation (Target values)
                    double target = (x1 * 0.20) + (x2 * 0.30) + (x3 * 0.40) - (x4 * 0.12) - (x5 * 0.08) + 0.05;
                    target = Math.max(0.0, Math.min(1.0, target));

                    double prediction = (x1 * wWorkHours) + (x2 * wStress) + (x3 * wFatigue) + (x4 * wWorkLife) + (x5 * wPeerSupport) + bias;
                    double error = prediction - target;

                    loss += error * error;

                    dw1 += error * x1;
                    dw2 += error * x2;
                    dw3 += error * x3;
                    dw4 += error * x4;
                    dw5 += error * x5;
                    db += error;
                }

                double n = dataset.size();
                wWorkHours -= learningRate * (dw1 / n);
                wStress -= learningRate * (dw2 / n);
                wFatigue -= learningRate * (dw3 / n);
                wWorkLife -= learningRate * (dw4 / n);
                wPeerSupport -= learningRate * (dw5 / n);
                bias -= learningRate * (db / n);

                if (epoch == 1 || epoch == 10 || epoch == 50 || epoch == 100) {
                    System.out.printf("  Epoch %3d/%d - Mean Squared Error (MSE): %.6f\n", epoch, epochs, (loss / n));
                }
            }
            System.out.println("[Model] Optimization Completed successfully!");
            System.out.printf("  Updated parameters -> WorkHours weight: %.3f, Stress weight: %.3f, Fatigue weight: %.3f, Bias: %.3f\n",
                    wWorkHours, wStress, wFatigue, bias);
        }
    }

    // ==========================================
    // 6. DATA STRUCTURES (BST & Max-Heap)
    // ==========================================
    
    /**
     * Binary Search Tree indexing employees by Burnout Index.
     * Enables fast range search queries.
     */
    public static class EmployeeBST {
        private static class Node {
            Employee employee;
            Node left, right;

            Node(Employee employee) {
                this.employee = employee;
            }
        }

        private Node root;

        public void insert(Employee emp) {
            root = insertRec(root, emp);
        }

        private Node insertRec(Node node, Employee emp) {
            if (node == null) {
                return new Node(emp);
            }
            // Sort by burnout index score
            if (emp.getBurnoutIndex() < node.employee.getBurnoutIndex()) {
                node.left = insertRec(node.left, emp);
            } else {
                node.right = insertRec(node.right, emp);
            }
            return node;
        }

        public List<Employee> rangeSearch(double minScore, double maxScore) {
            List<Employee> results = new ArrayList<>();
            rangeSearchRec(root, minScore, maxScore, results);
            return results;
        }

        private void rangeSearchRec(Node node, double min, double max, List<Employee> results) {
            if (node == null) return;

            // Inorder traversal to retrieve results in sorted order
            if (node.employee.getBurnoutIndex() > min) {
                rangeSearchRec(node.left, min, max, results);
            }

            if (node.employee.getBurnoutIndex() >= min && node.employee.getBurnoutIndex() <= max) {
                results.add(node.employee);
            }

            if (node.employee.getBurnoutIndex() < max) {
                rangeSearchRec(node.right, min, max, results);
            }
        }

        public void clear() {
            root = null;
        }
    }

    /**
     * Custom Array-based Max-Heap (Priority Queue) sorted by Burnout Index.
     * Easily isolates top burnout-risk cases.
     */
    public static class MaxHeap {
        private Employee[] heap;
        private int size;
        private int capacity;

        public MaxHeap(int capacity) {
            this.capacity = capacity;
            this.heap = new Employee[capacity];
            this.size = 0;
        }

        public void insert(Employee emp) {
            if (size >= capacity) {
                capacity *= 2;
                Employee[] newHeap = new Employee[capacity];
                System.arraycopy(heap, 0, newHeap, 0, size);
                heap = newHeap;
            }
            heap[size] = emp;
            bubbleUp(size);
            size++;
        }

        public Employee extractMax() {
            if (size == 0) return null;
            Employee maxVal = heap[0];
            heap[0] = heap[size - 1];
            size--;
            bubbleDown(0);
            return maxVal;
        }

        private void bubbleUp(int index) {
            while (index > 0) {
                int parent = (index - 1) / 2;
                if (heap[index].getBurnoutIndex() > heap[parent].getBurnoutIndex()) {
                    swap(index, parent);
                    index = parent;
                } else {
                    break;
                }
            }
        }

        private void bubbleDown(int index) {
            while (2 * index + 1 < size) {
                int left = 2 * index + 1;
                int right = 2 * index + 2;
                int largest = index;

                if (heap[left].getBurnoutIndex() > heap[largest].getBurnoutIndex()) {
                    largest = left;
                }
                if (right < size && heap[right].getBurnoutIndex() > heap[largest].getBurnoutIndex()) {
                    largest = right;
                }

                if (largest != index) {
                    swap(index, largest);
                    index = largest;
                } else {
                    break;
                }
            }
        }

        private void swap(int i, int j) {
            Employee temp = heap[i];
            heap[i] = heap[j];
            heap[j] = temp;
        }

        public boolean isEmpty() {
            return size == 0;
        }
    }

    // ==========================================
    // 7. FILE HANDLING MODULE
    // ==========================================
    public static class ReportGenerator {
        public static void exportReportToFile(String filename, List<Employee> employees) throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                writer.write("========================================================================\n");
                writer.write("                  EMPLOYEE BURNOUT DETAILED REPORT\n");
                writer.write("========================================================================\n");
                writer.write("Generated on: " + new Date() + "\n");
                writer.write("Total Employees Tracked: " + employees.size() + "\n\n");

                if (employees.isEmpty()) {
                    writer.write("No employee metrics found.\n");
                    return;
                }

                // Stats calculation
                double totalHours = 0;
                double totalBurnout = 0;
                Map<String, List<Employee>> deptMap = new LinkedHashMap<>();

                for (Employee emp : employees) {
                    totalHours += emp.getWorkHoursPerDay();
                    totalBurnout += emp.getBurnoutIndex();
                    deptMap.computeIfAbsent(emp.getDepartment(), k -> new ArrayList<>()).add(emp);
                }

                writer.write(String.format("Average Daily Working Hours: %.2f hrs\n", (totalHours / employees.size())));
                writer.write(String.format("Average Burnout Index Score: %.4f\n\n", (totalBurnout / employees.size())));

                writer.write("--- Department Overview ---\n");
                writer.write(String.format("%-15s | %-12s | %-16s | %-16s\n", "Department", "Staff Count", "Avg Work Hours", "Avg Burnout Score"));
                writer.write("------------------------------------------------------------------------\n");
                for (Map.Entry<String, List<Employee>> entry : deptMap.entrySet()) {
                    double deptHours = 0;
                    double deptBurnout = 0;
                    for (Employee e : entry.getValue()) {
                        deptHours += e.getWorkHoursPerDay();
                        deptBurnout += e.getBurnoutIndex();
                    }
                    writer.write(String.format("%-15s | %-12d | %-16.2f | %-16.4f\n",
                            entry.getKey(),
                            entry.getValue().size(),
                            (deptHours / entry.getValue().size()),
                            (deptBurnout / entry.getValue().size())));
                }

                // Risk categorization
                int highCount = 0, midCount = 0, lowCount = 0;
                for (Employee e : employees) {
                    if (e.getBurnoutIndex() >= 0.70) highCount++;
                    else if (e.getBurnoutIndex() >= 0.40) midCount++;
                    else lowCount++;
                }

                writer.write("\n--- Burnout Risk Categories Breakdown ---\n");
                writer.write(String.format("  - Imminent/High Risk (>= 0.70): %d employees (%.1f%%)\n", highCount, (highCount * 100.0 / employees.size())));
                writer.write(String.format("  - Moderate Warning (0.40-0.69): %d employees (%.1f%%)\n", midCount, (midCount * 100.0 / employees.size())));
                writer.write(String.format("  - Healthy/Low Risk (< 0.40):  %d employees (%.1f%%)\n\n", lowCount, (lowCount * 100.0 / employees.size())));

                writer.write("--- Detailed Risk Roster (Sorted Descending) ---\n");
                writer.write(String.format("%-5s | %-16s | %-14s | %-12s | %-12s\n", "ID", "Employee Name", "Department", "Fatigue (0-10)", "Burnout Score"));
                writer.write("------------------------------------------------------------------------\n");

                List<Employee> roster = new ArrayList<>(employees);
                roster.sort((e1, e2) -> Double.compare(e2.getBurnoutIndex(), e1.getBurnoutIndex()));
                for (Employee e : roster) {
                    writer.write(String.format("%-5d | %-16s | %-14s | %-14.1f | %-12.4f\n",
                            e.getId(), e.getName(), e.getDepartment(), e.getMentalFatigue(), e.getBurnoutIndex()));
                }
                writer.write("========================================================================\n");
            }
        }
    }

    // ==========================================
    // 8. INTERACTIVE SYSTEM CONTROLLER & CLI
    // ==========================================
    private static void syncStructures() {
        employeeBST.clear();
        List<Employee> list = dbManager.getAllEmployees();
        for (Employee emp : list) {
            employeeBST.insert(emp);
        }
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("       EMPLOYEE BURNOUT PREDICTION SYSTEM (OOP & DSA)      ");
        System.out.println("==========================================================");

        dbManager = new DatabaseManager();
        dbManager.initializeDatabase();
        syncStructures();

        while (true) {
            System.out.println("\n----------------------------------------------------------");
            System.out.println("  SYSTEM MAIN MENU  (" + (dbManager.isMockMode() ? "Mode: Mock Data Store" : "Mode: MySQL Connection") + ")");
            System.out.println("----------------------------------------------------------");
            System.out.println("1. Register Employee & Run Burnout Prediction");
            System.out.println("2. Lookup Employee Details by ID");
            System.out.println("3. List All Employees & Burnout Indicators");
            System.out.println("4. Display Top N Imminent Burnout Risks (Max-Heap)");
            System.out.println("5. Search Burnout Index Range (BST Range Query)");
            System.out.println("6. Optimize Predictor Parameters (Gradient Descent)");
            System.out.println("7. Export Analytical Summary Report to File");
            System.out.println("8. Exit System");
            System.out.print("\nSelect Option (1-8): ");

            String input = scanner.nextLine();
            int choice = -1;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[Error] Please enter a valid number from 1 to 8.");
                continue;
            }

            try {
                switch (choice) {
                    case 1:
                        registerEmployeeFlow();
                        break;
                    case 2:
                        lookupEmployeeFlow();
                        break;
                    case 3:
                        listAllEmployeesFlow();
                        break;
                    case 4:
                        displayHighRisksFlow();
                        break;
                    case 5:
                        searchBstRangeFlow();
                        break;
                    case 6:
                        trainModelFlow();
                        break;
                    case 7:
                        exportReportFlow();
                        break;
                    case 8:
                        System.out.println("\nShutting down Employee Burnout Prediction System. Goodbye!");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("[Error] Unknown Menu option. Choose 1 to 8.");
                }
            } catch (Exception e) {
                System.out.println("\n[Execution Error] " + e.getMessage());
            }
        }
    }

    private static void registerEmployeeFlow() {
        System.out.println("\n--- Register Employee & Run Predictor ---");
        try {
            System.out.print("Enter Employee ID (Integer): ");
            int id = Integer.parseInt(scanner.nextLine());
            InputValidator.validateId(id);

            // Check duplicate
            if (dbManager.getEmployeeById(id) != null) {
                throw new ValidationException("Employee with ID " + id + " already exists in the system.");
            }

            System.out.print("Enter Full Name: ");
            String name = scanner.nextLine();
            InputValidator.validateName(name);

            System.out.print("Enter Corporate Email: ");
            String email = scanner.nextLine();
            InputValidator.validateEmail(email);

            System.out.print("Enter Department: ");
            String dept = scanner.nextLine();
            InputValidator.validateDepartment(dept);

            System.out.print("Daily Working Hours (0.0 - 24.0): ");
            double hours = Double.parseDouble(scanner.nextLine());
            InputValidator.validateWorkHours(hours);

            System.out.print("Stress Level (1 [Low] - 10 [Extreme]): ");
            int stress = Integer.parseInt(scanner.nextLine());
            InputValidator.validateStressLevel(stress);

            System.out.print("Work-Life Balance (1 [Poor] - 5 [Excellent]): ");
            int wlb = Integer.parseInt(scanner.nextLine());
            InputValidator.validateWorkLifeBalance(wlb);

            System.out.print("Peer & Management Support (1 [None] - 5 [Strong]): ");
            int support = Integer.parseInt(scanner.nextLine());
            InputValidator.validatePeerSupport(support);

            System.out.print("Mental Fatigue Level (0.0 [None] - 10.0 [Extreme]): ");
            double fatigue = Double.parseDouble(scanner.nextLine());
            InputValidator.validateMentalFatigue(fatigue);

            // Construct entity
            Employee emp = new Employee(id, name, email, dept, hours, stress, wlb, support, fatigue);
            double calculatedScore = emp.predictBurnout();

            // Save to DB and refresh structures
            dbManager.saveEmployee(emp);
            syncStructures();

            System.out.println("\n[Success] Employee successfully registered.");
            System.out.printf("  Calculated Burnout Index: %.4f\n", calculatedScore);
            if (calculatedScore >= 0.70) {
                System.out.println("  [ALERT] This employee is categorized as Imminent Risk! Recommend intervention.");
            } else if (calculatedScore >= 0.40) {
                System.out.println("  [WARNING] This employee is showing warning signs of fatigue.");
            } else {
                System.out.println("  [Info] This employee shows healthy stress metrics.");
            }

        } catch (NumberFormatException e) {
            System.out.println("[Error] Numerical parsing error. Please enter correct data types.");
        } catch (ValidationException e) {
            System.out.println("[Validation Error] " + e.getMessage());
        }
    }

    private static void lookupEmployeeFlow() {
        System.out.println("\n--- Lookup Employee ---");
        System.out.print("Enter Employee ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Employee emp = dbManager.getEmployeeById(id);
            if (emp == null) {
                System.out.println("[Info] Employee not found.");
            } else {
                System.out.println("\nEmployee Record Found:");
                System.out.println("------------------------------------------------------------------------");
                System.out.println("ID:                 " + emp.getId());
                System.out.println("Name:               " + emp.getName());
                System.out.println("Email:              " + emp.getEmail());
                System.out.println("Department:         " + emp.getDepartment());
                System.out.println("Working Hours:      " + emp.getWorkHoursPerDay() + " hrs/day");
                System.out.println("Stress Level:       " + emp.getStressLevel() + "/10");
                System.out.println("Work-Life Balance:  " + emp.getWorkLifeBalance() + "/5");
                System.out.println("Peer Support:       " + emp.getPeerSupport() + "/5");
                System.out.println("Mental Fatigue:     " + emp.getMentalFatigue() + "/10");
                System.out.printf("Burnout Index Score: %.4f\n", emp.getBurnoutIndex());
                System.out.println("------------------------------------------------------------------------");
            }
        } catch (NumberFormatException e) {
            System.out.println("[Error] ID must be an integer.");
        }
    }

    private static void listAllEmployeesFlow() {
        System.out.println("\n--- Organization Burnout Roster ---");
        List<Employee> list = dbManager.getAllEmployees();
        if (list.isEmpty()) {
            System.out.println("No employees registered in the system.");
            return;
        }

        System.out.println("--------------------------------------------------------------------------------------------------------");
        for (Employee emp : list) {
            emp.displayDetails();
        }
        System.out.println("--------------------------------------------------------------------------------------------------------");
        System.out.println("Total Staff Registered: " + list.size());
    }

    private static void displayHighRisksFlow() {
        System.out.println("\n--- Top Imminent Burnout Risks (Max-Heap Analysis) ---");
        List<Employee> list = dbManager.getAllEmployees();
        if (list.isEmpty()) {
            System.out.println("No employees in system.");
            return;
        }

        System.out.print("Enter number of records to retrieve (N): ");
        try {
            int n = Integer.parseInt(scanner.nextLine());
            if (n <= 0) {
                System.out.println("[Error] Count N must be positive.");
                return;
            }

            // Build temporary Max Heap
            MaxHeap tempHeap = new MaxHeap(list.size());
            for (Employee e : list) {
                tempHeap.insert(e);
            }

            System.out.println("\nTop " + Math.min(n, list.size()) + " Employees at Risk of Burnout:");
            System.out.println("--------------------------------------------------------------------------------------------------------");
            int count = 0;
            while (!tempHeap.isEmpty() && count < n) {
                Employee emp = tempHeap.extractMax();
                if (emp != null) {
                    System.out.printf("[Rank %d] ", ++count);
                    emp.displayDetails();
                }
            }
            System.out.println("--------------------------------------------------------------------------------------------------------");
        } catch (NumberFormatException e) {
            System.out.println("[Error] Count must be an integer.");
        }
    }

    private static void searchBstRangeFlow() {
        System.out.println("\n--- Search Burnout score Range (BST Range Query) ---");
        try {
            System.out.print("Enter Minimum Burnout Index (0.0 - 1.0): ");
            double min = Double.parseDouble(scanner.nextLine());
            System.out.print("Enter Maximum Burnout Index (0.0 - 1.0): ");
            double max = Double.parseDouble(scanner.nextLine());

            if (min < 0.0 || max > 1.0 || min > max) {
                System.out.println("[Error] Invalid bounds. Verify 0.0 <= min <= max <= 1.0");
                return;
            }

            List<Employee> matches = employeeBST.rangeSearch(min, max);
            System.out.println("\nEmployees within Burnout Range [" + min + " - " + max + "] (Sorted Ascending):");
            System.out.println("--------------------------------------------------------------------------------------------------------");
            if (matches.isEmpty()) {
                System.out.println("No employees found in this risk bracket.");
            } else {
                for (Employee emp : matches) {
                    emp.displayDetails();
                }
            }
            System.out.println("--------------------------------------------------------------------------------------------------------");
            System.out.println("Total Matches: " + matches.size());
        } catch (NumberFormatException e) {
            System.out.println("[Error] Range boundaries must be floating point numbers.");
        }
    }

    private static void trainModelFlow() {
        List<Employee> dataset = dbManager.getAllEmployees();
        BurnoutPredictor.trainModel(dataset);
        
        // Recompute predictions based on newly updated model parameter values
        for (Employee emp : dataset) {
            emp.predictBurnout();
            dbManager.saveEmployee(emp);
        }
        syncStructures();
        System.out.println("[System Info] Re-evaluated burnout indices for all employees using optimized weights.");
    }

    private static void exportReportFlow() {
        System.out.println("\n--- Export Report to File ---");
        String filename = "burnout_report.txt";
        try {
            List<Employee> list = dbManager.getAllEmployees();
            ReportGenerator.exportReportToFile(filename, list);
            System.out.println("[Success] Analytical report exported successfully to file: " + filename);
            System.out.println("          (Check " + filename + " in your project folder)");
        } catch (IOException e) {
            System.err.println("[Error] Failed to write report file: " + e.getMessage());
        }
    }
}
