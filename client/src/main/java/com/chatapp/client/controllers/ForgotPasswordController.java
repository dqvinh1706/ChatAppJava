package com.chatapp.client.controllers;

import com.chatapp.client.components.CustomPasswordField.CustomPasswordField;
import com.chatapp.client.components.CustomTextField.CustomTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ForgotPasswordController implements Initializable {

    @FXML
    private VBox container;

    @FXML
    private Label descriptLabel;
    @FXML
    private Button btn;
    private CustomTextField email;

    private CustomTextField otp;
    private CustomPasswordField password;
    private CustomPasswordField confirmPassword;

    private CustomTextField createTextField() {
        CustomTextField usernameField = new CustomTextField();
        usernameField.setPrefWidth(343);

        usernameField.setBeforeIcon("mdi-account:32");
        usernameField.setAfterIcon("mdi-close:32");

        usernameField.getPromptText().set("Email");
        return usernameField;
    }

    private CustomPasswordField createCustomPasswordField() {
        CustomPasswordField passwordField = new CustomPasswordField();
        passwordField.setPrefWidth(343);
        passwordField.getPromptText().set("Password");
        return passwordField;
    }

    private void firstStep() {
        descriptLabel.setText("Enter Your email and we will send otp to you");
        email = createTextField();
        container.getChildren().add(3, email);
        VBox.setMargin(email, new Insets(0, 0, 10, 0));

        btn.setText("Send My Password");
        btn.setOnAction(e -> {
            onSendPassword();
        });
    }

    private void secondStep() {
        container.getChildren().remove(email);

        otp = createTextField();
        otp.getPromptText().set("OTP");
        descriptLabel.setText("Your new password has been send to Your email.\n" +
                "Please check Your inbox.");

        password = createCustomPasswordField();
        password.getPromptText().set("New password");
        confirmPassword = createCustomPasswordField();
        confirmPassword.getPromptText().set("Confirm password");

        container.getChildren().add(3, otp);
        container.getChildren().add(4, password);
        container.getChildren().add(5, confirmPassword);

        VBox.setMargin(otp, new Insets(0, 0, 5, 0));
        VBox.setMargin(password, new Insets(0, 0, 5, 0));

        btn.setText("Reset password");
        btn.setOnAction(e -> {
            onResetPassword();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        firstStep();
    }

    private void onSendPassword() {
        secondStep();
    }

    private void onResetPassword() {

    }
}
