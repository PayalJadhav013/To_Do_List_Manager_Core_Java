package com.todolist;

import java.util.Date;

public class Task {
    private int id;
    private String name;
    private String description;
    private Date dueDate;
    private String status;
    private Date completionDate; // New field

    // Constructor
    public Task(int id, String name, String description, Date dueDate, String status, Date completionDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.completionDate = completionDate;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Date getDueDate() { return dueDate; }
    public String getStatus() { return status; }
    public Date getCompletionDate() { return completionDate; } // New getter

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public void setStatus(String status) { this.status = status; }
    public void setCompletionDate(Date completionDate) { this.completionDate = completionDate; } // New setter

    // To String (for easy printing)
    @Override
    public String toString() {
        return "📌 Task ID: " + id +
               "\n🔹 Name: " + name +
               "\n📝 Description: " + description +
               "\n📅 Due Date: " + dueDate +
               "\n✅ Status: " + status +
               "\n🏁 Completion Date: " + completionDate + "\n";
    }
}