package com.bjergfelt.himev5.Chat;

/**
 * Created by andersbjergfelt on 06/05/2016.
 */
import com.bjergfelt.himev5.model.User;

import java.io.Serializable;

public class Message implements Serializable {
    String id, message, createdAt;
    User user;
    User toUser;

    public Message() {
    }

    public Message(String id, String message, String createdAt, User user) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
        this.user = user;
    }
    public Message(String id, String message, String createdAt, User user, User toUser) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
        this.user = user;
        this.toUser = toUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
