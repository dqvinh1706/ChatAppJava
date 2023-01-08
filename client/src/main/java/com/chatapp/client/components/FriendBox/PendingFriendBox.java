package com.chatapp.client.components.FriendBox;

import com.chatapp.commons.models.User;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.Blob;

public class PendingFriendBox extends FriendBox {

    @Setter @Getter
    private FriendBoxType type = FriendBoxType.ACCEPT_FRIEND;
    private Button acceptBtn = new Button(), cancelBtn = new Button();
    private FontIcon acceptIcon, cancelIcon;

    public PendingFriendBox() {
        super();
        initGui();
    }

    public PendingFriendBox(FriendBoxType type) {
        this();
        setType(type);
    }

    public void setType(FriendBoxType type) {
        if (type == FriendBoxType.UNFRIEND) {
            disableAcceptBtn();
            cancelBtn.setTooltip(new Tooltip("Hủy kết bạn"));
        }
    }

    public static PendingFriendBox toPendingFriendBox(User user) {
        return PendingFriendBox.toPendingFriendBox(user, FriendBoxType.ACCEPT_FRIEND);
    }

   public static PendingFriendBox toPendingFriendBox(User user, FriendBoxType type) {
        // Convert a user model to PendingFriendBox
        PendingFriendBox ins = new PendingFriendBox(type);
        ins.setData(user);
        System.out.println(ins.getType());
        return ins;
    }

    private void initGui() {
        acceptBtn.setTooltip(new Tooltip("Chấp nhận"));
        acceptBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        acceptIcon = new FontIcon("mdi2c-check:24");
        acceptIcon.getStyleClass().add("accept-btn");
        acceptBtn.setGraphic(acceptIcon);

        cancelBtn.setTooltip(new Tooltip("Từ chối"));
        cancelBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        cancelIcon = new FontIcon("mdi2c-close-thick:24");
        cancelIcon.getStyleClass().add("cancel-btn");
        cancelBtn.setGraphic(cancelIcon);

        this.getChildren().addAll(acceptBtn, cancelBtn);
    }

    public void disableAcceptBtn() {
        this.getChildren().remove(acceptBtn);
    }

    public void setAcceptBtnAction(EventHandler eventHandler) {
        this.acceptBtn.setOnAction(eventHandler);
    }

    public void setCancelBtnAction(EventHandler eventHandler) {
        this.cancelBtn.setOnAction(eventHandler);
    }
}
