package com.chatapp.controllers;

import com.chatapp.components.CustomPasswordField.CustomPasswordField;
import com.chatapp.components.CustomTextField.CustomTextField;
import com.chatapp.validations.EmailValidator;
import com.chatapp.validations.PasswordValidator;
import com.chatapp.validations.UsernameValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;

import java.net.URL;
import java.util.ResourceBundle;

public class SignupController  implements Initializable {
    @FXML
    private GridPane container;

    private CustomTextField email;
    private CustomTextField username;
    private CustomPasswordField password;
    private CustomPasswordField confirmPassword;

    @FXML
    private Button signInBtn;

    @FXML
    private Button signUpBtn;

    private Validator validator = new Validator();

    private CustomTextField createTextField() {
        CustomTextField textField = new CustomTextField();
        textField.setPrefWidth(300);
        textField.setBeforeIcon("mdi-account:32");
        textField.setAfterIcon("mdi-close:32");

        return textField;
    }
    private CustomPasswordField createCustomPasswordField() {
        CustomPasswordField passwordField = new CustomPasswordField();
        passwordField.setPrefWidth(300);
        return passwordField;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ScreenController screenController = ScreenController.getInstance();

        email = createTextField();
        email.setBeforeIcon("mdi-email:32");
        email.getPromptText().set("Email");

        username = createTextField();
        username.getPromptText().set("Username");

        password = createCustomPasswordField();
        password.getPromptText().set("Password");

        confirmPassword = createCustomPasswordField();
        confirmPassword.getPromptText().set("Confirm password");

        container.add(username, 0, 0);
        container.add(email, 1, 0);
        container.add(password, 0, 1);
        container.add(confirmPassword, 1, 1);
        setupValidation();

    }

    private void setupValidation() {
        validator.createCheck()
                .withMethod(c -> {
                    try {
                        UsernameValidator.isValid((String) c.get("username"));
                    } catch (Exception e) {
                        c.error(e.getMessage());
                    }
                })
                .dependsOn("username", username.getTextProperty())
                .decoratingWith(this::errorDecorator)
                .decorates(username.getErrorLabel());

        validator.createCheck()
                .withMethod(c -> {
                    try {
                        EmailValidator.isValid((String) c.get("email"));
                    } catch (Exception e) {
                        c.error(e.getMessage());
                    }
                })
                .dependsOn("email", email.getTextProperty())
                .decoratingWith(this::errorDecorator)
                .decorates(email.getErrorLabel());

        validator.createCheck()
                .withMethod(c -> {
                    try {
                        PasswordValidator.isValid((String) c.get("password"));
                    } catch (Exception e) {
                        c.error(e.getMessage());
                    }
                })
                .dependsOn("password", password.getTextProperty())
                .decoratingWith(this::errorDecorator)
                .decorates(password.getErrorLabel());

        validator.createCheck()
                .withMethod(c -> {
                    if (!c.get("password").equals(c.get("confirmPassword"))) {
                        c.error("Password do not match");
                    }
                })
                .dependsOn("password", password.getTextProperty())
                .dependsOn("confirmPassword", confirmPassword.getTextProperty())
                .decoratingWith(this::errorDecorator)
                .decorates(confirmPassword.getErrorLabel());
    }

    private Decoration errorDecorator(ValidationMessage m) {
        return new Decoration() {
            @Override
            public void add(Node node) {
                ((Label) node).setText(m.getText());
            }

            @Override
            public void remove(Node node) {
                ((Label) node).setText("");
            }
        };
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        ScreenController.getInstance().switchToScreen("login");
    }

    @FXML
    private void onSignUp(ActionEvent event) {
        validator.validate();
    }
}
