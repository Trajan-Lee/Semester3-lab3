package com.algonquin.loggy.model;

import java.time.LocalDateTime;

public class Log {
    private int id;
    private String title;
    private String content;
    private LocalDateTime timestamp;

    public Log(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
