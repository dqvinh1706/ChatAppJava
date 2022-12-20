package com.chatapp.client.components.MessageContainer;

public class SendMessage  extends MessageBox {
    public SendMessage() { super(); }
    public SendMessage(int userId) { super(); setUserId(userId); this.setId("SendMessage");}
    public void initGui() {
        this.setStyle(
                "-message-bg-color: #0084ff;" +
                "-message-text-alignment: center-right;" +
                "-message-stack-alignment: bottom-right;" +
                "-message-text-color: #fff;"
        );
    }

    @Override
    public String getFirstMessageStyleClass() {
        return "first-sender-message";
    }

    @Override
    public String getMiddleMessageStyleClass() {
        return "middle-sender-message";
    }

    @Override
    public String getLassMessageStyleClass() {
        return "last-sender-message";
    }
}