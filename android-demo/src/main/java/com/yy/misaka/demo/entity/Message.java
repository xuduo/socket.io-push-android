package com.yy.misaka.demo.entity;

public class Message {

    private String message;
    private String nickName;
    private String type = "chat_message";

    public String getMessage() {
        return message;
    }

    public String getNickName() {
        return nickName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
