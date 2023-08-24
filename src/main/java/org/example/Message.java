package org.example;

import java.io.Serializable;

public class Message implements Serializable {
    int id;
    String message;
    public Message(int id, String message){
        this.id = id;
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", message='" + message + '\'' +
                '}';
    }
}
