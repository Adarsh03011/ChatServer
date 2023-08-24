package org.example;

import java.io.Serializable;

class ChatMessage implements Serializable {
    static final int Active = 0, Message = 1, Logout = 2;
    private int type;
    private Message message;

    ChatMessage(int type, Message message) {
        this.type = type;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public Message getMessage() {
        return message;
    }
}
