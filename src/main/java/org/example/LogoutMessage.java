package org.example;

public class LogoutMessage extends Message{
    public LogoutMessage(int id, String message) {
        super(id, message);
    }

    @Override
    public String toString() {
        return "LogoutMessage{" +
                "id=" + id +
                ", message='" + message + '\'' +
                '}';
    }
}
