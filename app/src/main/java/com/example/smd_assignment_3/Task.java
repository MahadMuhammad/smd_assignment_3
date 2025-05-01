package com.example.smd_assignment_3;

public class Task {
    private long id;
    private String title;
    private String description;
    private long datetime;
    private String status;

    public Task() {
    }

    public Task(long id, String title, String description, long datetime, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.datetime = datetime;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}