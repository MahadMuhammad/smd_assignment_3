package com.example.smd_assignment_3;


public class Notification {
    private long id;
    private String message;
    private long datetime;

    public Notification() {
    }

    public Notification(long id, String message, long datetime) {
        this.id = id;
        this.message = message;
        this.datetime = datetime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }
}
