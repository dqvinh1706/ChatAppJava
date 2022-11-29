package com.chatapp.components.CustomTextField;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class CustomTextField extends VBox implements Initializable {
    @FXML
    private Label afterIcon;

    @FXML
    private Label beforeIcon;

    @FXML
    private Label errorText;

    @FXML
    private TextField textInput;

    private HBox textInputContainer;

    public CustomTextField() {
        FXMLLoader loader = new FXMLLoader(CustomTextField.class.getResource("CustomTextField.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try{
            loader.load();
            this.getStyleClass().add("custom-text-field");
        }catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        this.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        afterIcon.setVisible(false);

        textInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            boolean iconVisible = textInput.getText().isBlank();
            if (newValue) {
                afterIcon.setVisible(!iconVisible);
                textInputContainer.getStyleClass().add("is-focus");
            }
            else{
                afterIcon.setVisible(false);
                textInputContainer.getStyleClass().remove("is-focus");
            }
        });

        textInput.textProperty().addListener((observable, oldValue, newValue) -> {
            afterIcon.setVisible(!Objects.equals(newValue, ""));
        });

        afterIcon.setOnMouseClicked(e -> {
            if (!textInput.getText().isBlank()) {
                textInput.clear();
            }
        });

        textInputContainer = (HBox) this.textInput.getParent();
        textInputContainer.setOnMouseClicked(e -> {
            textInput.requestFocus();
        });
    }

    public void setAfterIcon(String iconLiteral) {
        ((FontIcon) this.afterIcon.getGraphic()).setIconLiteral(iconLiteral);
    }

    public void setBeforeIcon(String iconLiteral) {
        ((FontIcon) this.beforeIcon.getGraphic()).setIconLiteral(iconLiteral);

    }

    public StringProperty getPromptText() {
        return this.textInput.promptTextProperty();
    }

    public StringProperty getTextProperty() {
        return this.textInput.textProperty();
    }

    public Label getErrorLabel() {
        return this.errorText;
    }
}
