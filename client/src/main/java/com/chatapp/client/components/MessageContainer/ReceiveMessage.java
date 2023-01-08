package com.chatapp.client.components.MessageContainer;

import com.chatapp.client.components.Avatar.Avatar;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;

public class ReceiveMessage extends MessageBox {
    private BorderPane avatarContainer;
    private String senderName;

    public ReceiveMessage(int userId, String senderName) {
        super(userId);
        this.setId("ReceiveMessage");
        this.senderName = senderName;
        Tooltip.install(avatarContainer, new Tooltip(senderName));
    }

    @Override
    public void initGui() {
        this.setStyle(
                "-message-bg-color: #E4E6EB;" +
                        "-message-text-alignment: center-left;" +
                        "-message-stack-alignment: bottom-left;" +
                        "-message-text-color: #050505;"
        );
        avatarContainer = new BorderPane();
        avatarContainer.setCenter(new Avatar(28, 28));
        avatarContainer.getStyleClass().add("avatar-container");
        this.getChildren().add(0, avatarContainer);
    }

    @Override
    public String getFirstMessageStyleClass() {
        return "first-receive-message";
    }

    @Override
    public String getMiddleMessageStyleClass() {
        return "middle-receive-message";
    }

    @Override
    public String getLassMessageStyleClass() {
        return "last-receive-message";
    }
}
