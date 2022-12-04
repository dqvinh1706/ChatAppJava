package com.chatapp.components.Avatar;

import com.chatapp.Main;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Avatar extends AnchorPane implements Initializable {
    @FXML
    private Label activeSymbol;
    @FXML
    private ImageView image;

    private SimpleBooleanProperty isActive = new SimpleBooleanProperty(false);

    public Avatar() {
        FXMLLoader loader = new FXMLLoader(Avatar.class.getResource("Avatar.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try{
            loader.load();

            this.getStyleClass().add("user-avatar");
            this.prefHeightProperty().bind(this.heightProperty());
            this.prefWidthProperty().bind(this.widthProperty());

            this.image.fitHeightProperty().bind(this.heightProperty());
            this.image.fitWidthProperty().bind(this.widthProperty());

            this.activeSymbol.prefWidthProperty().bind(Bindings.multiply(0.25, this.widthProperty()));
            this.activeSymbol.prefHeightProperty().bind(this.activeSymbol.prefWidthProperty());

            this.activeSymbol.visibleProperty().bind(isActive);
        }catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    public Avatar(double width, double height) {
        this();
        setAvatarWidth(width);
        setAvatarHeight(height);
        setImage(Main.class.getResource("imgs/default_avatar.jpg").toString());
    }

    public Avatar(Image avatar) {
        this(48, 48);
        setImage(avatar);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void circleImage() {
        final Circle clip = new Circle(image.getFitWidth() / 2, image.getFitWidth() / 2, image.getFitWidth() / 2);
        this.image.setClip(clip);
    }

    public void setImage(String imagePath) {
        this.image.setImage(new Image(imagePath));
        this.circleImage();
    }

    public void setImage(Image image) {
        this.image.setImage(image);
        this.circleImage();
    }

    public void setAvatarWidth(double width) {
        this.setWidth(width);
    }
    public void setAvatarHeight(double height) {
        this.setHeight(height);
    }

    public void setSize(double width, double height) {
        this.setAvatarWidth(width);
        this.setAvatarHeight(height);
    }

    public void setActiveStatus(boolean isActive) {
        this.isActive.set(isActive);
    }

    public SimpleBooleanProperty isActiveProperty() {
        return isActive;
    }

    public Image getImage() {

        return this.image.getImage();
    }

    public ObjectProperty<Image> imageProperty() {
        return this.image.imageProperty();
    }

    public void disabledActiveSymbol() {
        ((Pane) activeSymbol.getParent()).getChildren().remove(activeSymbol);
    }
}
