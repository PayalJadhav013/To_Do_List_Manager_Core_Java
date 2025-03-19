package com.todolist;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- To-Do List Manager ---");
            System.out.println("1. Add Task");
            System.out.println("2. View Tasks");
            System.out.println("3. Update Task");
            System.out.println("4. Delete Task");
            System.out.println("5. Mark Task as Completed");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    addTask();
                    break;
                case 2:
                    viewTasks();
                    break;
                case 3:
                    updateTask();
                    break;
                case 4:
                    deleteTask();
                    break;
                case 5:
                    markTaskAsCompleted();
                    break;
                case 6:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void addTask() {
        System.out.print("Enter task ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Check if the ID already exists
        if (taskIdExists(id)) {
            System.out.println("❌ Task ID already exists. Please choose a different ID.");
            return;
        }

        System.out.print("Enter task name: ");
        String name = scanner.nextLine();
        System.out.print("Enter task description: ");
        String description = scanner.nextLine();

        // Get due date from user
        Date dueDate = getValidDate("Enter due date (yyyy-MM-dd): ");
        if (dueDate == null) {
            System.out.println("❌ Task creation failed due to invalid date.");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO tasks (id, name, description, due_date, status) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setString(3, description);
            stmt.setDate(4, new java.sql.Date(dueDate.getTime()));
            stmt.setString(5, "Pending");

            stmt.executeUpdate();
            System.out.println("✅ Task added successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void viewTasks() {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tasks")) {

            System.out.println("\n--- Task List ---");
            while (rs.next()) {
                System.out.println("📌 ID: " + rs.getInt("id"));
                System.out.println("🔹 Name: " + rs.getString("name"));
                System.out.println("📝 Description: " + rs.getString("description"));
                System.out.println("📅 Due Date: " + rs.getDate("due_date"));
                System.out.println("✅ Status: " + rs.getString("status"));
                System.out.println("🏁 Completion Date: " + rs.getDate("completion_date"));
                System.out.println("-----------------------------"); // Separator for readability
            }
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void updateTask() {
        System.out.print("Enter Task ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Prompt user for fields to update
        System.out.println("Which fields do you want to update? (Enter numbers separated by commas)");
        System.out.println("1. Name");
        System.out.println("2. Description");
        System.out.println("3. Due Date");
        System.out.println("4. Status");
        System.out.print("Your choice: ");
        String choices = scanner.nextLine();

        // Split the user's choices into an array
        String[] fieldsToUpdate = choices.split(",");

        // Variables to hold updated values
        String name = null;
        String description = null;
        Date dueDate = null;
        String status = null;

        // Process each choice
        for (String choice : fieldsToUpdate) {
            switch (choice.trim()) {
                case "1":
                    System.out.print("Enter new task name: ");
                    name = scanner.nextLine();
                    break;
                case "2":
                    System.out.print("Enter new task description: ");
                    description = scanner.nextLine();
                    break;
                case "3":
                    dueDate = getValidDate("Enter new due date (yyyy-MM-dd): ");
                    if (dueDate == null) {
                        System.out.println("❌ Invalid date format. Update failed.");
                        return;
                    }
                    break;
                case "4":
                    System.out.print("Enter new status (Pending/Completed): ");
                    status = scanner.nextLine();
                    break;
                default:
                    System.out.println("❌ Invalid choice: " + choice.trim());
                    return;
            }
        }

        // Build the SQL query dynamically based on the fields to update
        StringBuilder sql = new StringBuilder("UPDATE tasks SET ");
        boolean firstField = true;

        if (name != null) {
            sql.append("name = ?");
            firstField = false;
        }
        if (description != null) {
            if (!firstField) sql.append(", ");
            sql.append("description = ?");
            firstField = false;
        }
        if (dueDate != null) {
            if (!firstField) sql.append(", ");
            sql.append("due_date = ?");
            firstField = false;
        }
        if (status != null) {
            if (!firstField) sql.append(", ");
            sql.append("status = ?");
        }

        sql.append(" WHERE id = ?");

        // Execute the update
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            if (name != null) {
                stmt.setString(paramIndex++, name);
            }
            if (description != null) {
                stmt.setString(paramIndex++, description);
            }
            if (dueDate != null) {
                stmt.setDate(paramIndex++, new java.sql.Date(dueDate.getTime()));
            }
            if (status != null) {
                stmt.setString(paramIndex++, status);
            }

            stmt.setInt(paramIndex, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Task updated successfully!");
            } else {
                System.out.println("❌ Task ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void deleteTask() {
        System.out.print("Enter Task ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM tasks WHERE id = ?")) {
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Task deleted successfully!");
            } else {
                System.out.println("❌ Task ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void markTaskAsCompleted() {
        System.out.print("Enter Task ID to mark as completed: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE tasks SET status = 'Completed', completion_date = ? WHERE id = ?")) {
            stmt.setDate(1, new java.sql.Date(new Date().getTime())); // Set completion date to current date
            stmt.setInt(2, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Task marked as completed!");
            } else {
                System.out.println("❌ Task ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // Helper method to get a valid date from the user
    private static Date getValidDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String dateStr = scanner.nextLine();

            try {
                return dateFormat.parse(dateStr);
            } catch (ParseException e) {
                System.out.println("❌ Invalid date format. Please use yyyy-MM-dd.");
            }
        }
    }

    // Helper method to check if a task ID already exists
    private static boolean taskIdExists(int id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM tasks WHERE id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Returns true if the ID exists
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
            return true; // Assume ID exists to avoid conflicts
        }
    }
}