package com.chatapp.controllers;

import com.chatapp.components.CustomPasswordField.CustomPasswordField;
import com.chatapp.components.CustomTextField.CustomTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import net.synedra.validatorfx.Validator;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private VBox loginTab;

    private CustomTextField username;
    private CustomPasswordField password;

    @FXML
    private Button signInBtn;
    @FXML
    private Button forgotPassword;
    @FXML
    private Button signUpBtn;

    private Validator validator;

    public LoginController() {
    }

    private CustomTextField createUsernameField() {
        CustomTextField usernameField = new CustomTextField();
        usernameField.setPrefWidth(343);

        usernameField.setBeforeIcon("mdi-account:32");
        usernameField.setAfterIcon("mdi-close:32");
        usernameField.getPromptText().set("Username/Email");
        return usernameField;
    }

    private CustomPasswordField createCustomPasswordField() {
        CustomPasswordField passwordField = new CustomPasswordField();
        passwordField.setPrefWidth(343);
        passwordField.getPromptText().set("Password");
        return passwordField;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        username = createUsernameField();
        password = createCustomPasswordField();
        loginTab.getChildren().add(2, username);
        loginTab.getChildren().add(3, password);

        VBox.setMargin(username, new Insets(0, 0, 10, 0));

        ScreenController screenController = ScreenController.getInstance();
        signUpBtn.setOnAction(e -> {
            screenController.switchToScreen("signup");
        });
        forgotPassword.setOnAction(e -> {
            screenController.switchToScreen("forgotPassword");
        });

    }
}
