package com.chatapp.client.controllers;

import com.chatapp.client.Main;
import com.chatapp.client.SocketClient;
import com.chatapp.client.components.CustomPasswordField.CustomPasswordField;
import com.chatapp.client.components.CustomTextField.CustomTextField;
import com.chatapp.client.validations.EmailValidator;
import com.chatapp.client.validations.PasswordValidator;
import com.chatapp.client.validations.UsernameValidator;
import com.chatapp.commons.enums.Action;
import com.chatapp.commons.enums.StatusCode;
import com.chatapp.commons.request.AuthRequest;
import com.chatapp.commons.response.AuthResponse;
import com.chatapp.commons.response.Response;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
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
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/LoginView.fxml"));

        try {
            container.getScene().setRoot(loader.load());
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }

    @FXML
    private void onSignUp(ActionEvent event) {
        if (!validator.validate()) return;

        Properties formData = new Properties();
        formData.put("email", email.getText());
        formData.put("username", username.getText());
        formData.put("rawPassword", password.getText());

        try{
            SocketClient socketClient = SocketClient.getInstance();

            Task waitResponse = new Task() {
                @Override
                protected Response call() throws Exception {
                    socketClient.sendRequest(AuthRequest.builder()
                            .action(Action.SIGNUP)
                            .formData(formData)
                            .build()
                    );
                    return (Response) socketClient.getResponse();
                }
            };

            waitResponse.setOnSucceeded(e -> {
                AuthResponse res = (AuthResponse) waitResponse.getValue();
                if (res.getStatusCode() == StatusCode.AUTHENTICATED) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Sign up success");
                    alert.setHeaderText(null);
                    alert.setContentText("Congratulation, your account has been successfully created!");
                    alert.showAndWait();

                    goToLogin(event);
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Sign up error");
                    alert.setHeaderText(null);
                    alert.setContentText("User already exists!");
                    alert.show();
                }
            });

            Thread th = new Thread(waitResponse);
            th.setDaemon(true);
            th.start();
        }
        catch (IOException netError) {
            Platform.runLater(() -> {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Connection Error!");
                error.setContentText("Không thể kết nối đến Server!!!");
                error.setHeaderText(null);
                error.showAndWait();
            });
        }
    }
}
