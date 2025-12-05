package com.example.tarefas;

public class Task {
    private int id;
    private String title;
    private String description;
    private int priority;
    private String status;
    private String dateDue;

    public Task(int id, String title, String description, int priority, String status, String dateDue) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.dateDue = dateDue;
    }

    public Task(String title, String description, int priority, String status, String dateDue) {
        this(0, title, description, priority, status, dateDue);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDateDue() { return dateDue; }
    public void setDateDue(String dateDue) { this.dateDue = dateDue; }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace(";", "\\;");
    }
    private static String unesc(String s) {
        if (s == null) return "";
        return s.replace("\\;", ";").replace("\\\\", "\\");
    }

    public String toCsvLine() {
        return String.format("%d;%s;%s;%d;%s;%s",
                id,
                esc(title),
                esc(description),
                priority,
                status == null ? "" : status,
                dateDue == null ? "" : dateDue);
    }

    public static Task fromCsvLine(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length < 6) return null;
        try {
            int id = Integer.parseInt(parts[0]);
            String title = unesc(parts[1]);
            String desc = unesc(parts[2]);
            int pr = Integer.parseInt(parts[3]);
            String status = parts[4];
            String due = parts[5];
            return new Task(id, title, desc, pr, status, due);
        } catch (Exception e) {
            return null;
        }
    }
}
