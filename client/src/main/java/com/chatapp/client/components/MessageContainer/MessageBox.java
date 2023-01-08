package com.chatapp.client.components.MessageContainer;

import com.chatapp.commons.models.Message;
import com.chatapp.commons.utils.TimestampUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.*;

import java.io.IOException;

public abstract class MessageBox extends HBox {
    @Setter @Getter
    protected int userId;

    @FXML
    protected VBox messageStackContainer;

    public MessageBox() {
        FXMLLoader loader = new FXMLLoader(ReceiveMessage.class.getResource("MessageBox.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try{
            loader.load();
            initGui();
        }catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    public MessageBox(int userId) {
        this();
        setUserId(userId);
    }

    public abstract void initGui();
    public Label createMessageLabel(Message message){
        Label newMessageLabel = new Label(message.getMessage());
        newMessageLabel.setId("Message#" + message.getId());
        newMessageLabel.getStyleClass().add("message");

        Tooltip tooltip = new Tooltip(TimestampUtil.getTimeInDate(message.getCreatedAt()));
        newMessageLabel.setTooltip(tooltip);

        return newMessageLabel;
    }

    public abstract String getFirstMessageStyleClass();
    public abstract String getMiddleMessageStyleClass();
    public abstract String getLassMessageStyleClass();

    public void addTopMessage(Message message) {
        ObservableList<Node> messageLabels = messageStackContainer.getChildren();
        Label newMessageLabel = createMessageLabel(message);

        int size = messageLabels.size();
        if (size == 1) {
            messageLabels.get(0).getStyleClass().add(getLassMessageStyleClass());
            newMessageLabel.getStyleClass().add(getFirstMessageStyleClass());
        } else if (size > 1) {
            messageLabels.get(0).getStyleClass().remove(getFirstMessageStyleClass());
            messageLabels.get(0).getStyleClass().add(getMiddleMessageStyleClass());
            newMessageLabel.getStyleClass().add(getFirstMessageStyleClass());
        }

        messageLabels.add(0, newMessageLabel);
    }

    public void addBottomMessage(Message message) {
        ObservableList<Node> messageLabels = messageStackContainer.getChildren();
        Label newMessageLabel = createMessageLabel(message);
        int size = messageLabels.size();
        if (size == 1) {
            messageLabels.get(0).getStyleClass().add(getFirstMessageStyleClass());
            newMessageLabel.getStyleClass().add(getLassMessageStyleClass());
        } else if (size > 1) {
            messageLabels.get(size - 1).getStyleClass().remove(getLassMessageStyleClass());
            messageLabels.get(size - 1).getStyleClass().add(getMiddleMessageStyleClass());
            newMessageLabel.getStyleClass().add(getLassMessageStyleClass());
        }

        messageLabels.add(newMessageLabel);
    }
}
