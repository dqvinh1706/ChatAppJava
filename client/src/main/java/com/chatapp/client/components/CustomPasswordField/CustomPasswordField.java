package com.chatapp.client.components.CustomPasswordField;

import com.chatapp.client.Main;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.StackedFontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class CustomPasswordField extends VBox implements Initializable {
    @FXML
    private Label afterIcon;

    @FXML
    private Label beforeIcon;

    @FXML
    private Label errorText;

    @FXML
    private PasswordField hideInput;

    @FXML
    private HBox inputContainer;

    @FXML
    private TextField showInput;

    private FontIcon showPasswordIcon;
    private FontIcon hidePasswordIcon;


    public CustomPasswordField() {
        FXMLLoader loader = new FXMLLoader(CustomPasswordField.class.getResource("CustomPasswordField.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try{
            loader.load();
            this.getStyleClass().add("custom-password-field");
        }catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    private void configInputProperty() {
        this.showInput.promptTextProperty().bindBidirectional(this.hideInput.promptTextProperty());
        this.showInput.textProperty().bindBidirectional(this.hideInput.textProperty());
        this.showInput.visibleProperty().bind(Bindings.selectBoolean(this.hideInput.visibleProperty().not()));

        hideInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                inputContainer.getStyleClass().add("is-focus");
            }
            else{
                inputContainer.getStyleClass().remove("is-focus");
            }
        });
        showInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                inputContainer.getStyleClass().add("is-focus");
            }
            else{
                inputContainer.getStyleClass().remove("is-focus");
            }
        });

        inputContainer.setOnMouseClicked(e -> {
            boolean isShow = this.hideInput.isVisible();
            if (isShow) {
                this.hideInput.requestFocus();
                this.hideInput.positionCaret(this.hideInput.getText().length());
            }
            else {
                this.showInput.requestFocus();
                this.showInput.positionCaret(this.showInput.getText().length());

            }
        });
    }

    private void configIconProperty() {
        ((FontIcon) this.beforeIcon.getGraphic()).setIconLiteral("mdi-lock:32");
        StackedFontIcon stackedFontIcon = (StackedFontIcon) this.afterIcon.getGraphic();
        showPasswordIcon = new FontIcon("mdi-eye:32");
        hidePasswordIcon = new FontIcon("mdi-eye-off:32");

        stackedFontIcon.getChildren().add(showPasswordIcon);
        stackedFontIcon.getChildren().add(hidePasswordIcon);

        showPasswordIcon.visibleProperty().bind(
                Bindings.and(
                    Bindings.and(
                            hideInput.textProperty().isNotEmpty(),
                            hideInput.focusedProperty()
                    ),
                    hideInput.visibleProperty()
                )
        );

        hidePasswordIcon.visibleProperty().bind(
                Bindings.and(
                        Bindings.and(
                                showInput.textProperty().isNotEmpty(),
                                showInput.focusedProperty()
                        ),
                        showInput.visibleProperty()
                )
       );

        afterIcon.setOnMouseClicked(e -> {
            this.hideInput.setVisible(!this.hideInput.isVisible());
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        this.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        configInputProperty();
        configIconProperty();

        hideInput.setVisible(true);
    }

    public StringProperty getPromptText() {
        return this.hideInput.promptTextProperty();
    }

    public StringProperty getTextProperty() {
        return this.hideInput.textProperty();
    }

    public Label getErrorLabel() {
        return this.errorText;
    }

    public String getText() {
        return this.hideInput.getText();
    }
}
