package org.example;

import java.io.Serializable;

class ChatMessage implements Serializable {
    static final int Active = 0, Message = 1, Logout = 2;
    private int type;
    private String message;

    ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
