package com.chatapp.client.controllers;

import com.chatapp.client.Main;
import com.chatapp.client.SocketClient;
import com.chatapp.client.components.CustomPasswordField.CustomPasswordField;
import com.chatapp.client.components.CustomTextField.CustomTextField;
import com.chatapp.client.validations.EmailValidator;
import com.chatapp.client.validations.PasswordValidator;
import com.chatapp.client.validations.UsernameValidator;
import com.chatapp.client.workers.AuthSocketService;
import com.chatapp.client.workers.UserSocketService;
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
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private VBox loginTab;

    private CustomTextField username;
    private CustomPasswordField password;

    @FXML
    private Button signUpBtn, signInBtn;

    private final Validator validator = new Validator();

    public LoginController() {}

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

        signInBtn.setOnKeyReleased((KeyEvent e) -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                onLogin();
            }
        });

        VBox.setMargin(username, new Insets(0, 0, 10, 0));
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
                        PasswordValidator.isValid((String) c.get("password"));
                    } catch (Exception e) {
                        c.error(e.getMessage());
                    }
                })
                .dependsOn("password", password.getTextProperty())
                .decoratingWith(this::errorDecorator)
                .decorates(password.getErrorLabel());
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
    public void goToSignupView(ActionEvent e) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/SignupView.fxml"));

        try {
            signUpBtn.getScene().setRoot(loader.load());
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }

    @FXML
    public void goToForgotPassword(ActionEvent e) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/ForgotPasswordView.fxml"));

        try {
            signUpBtn.getScene().setRoot(loader.load());
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }
    @FXML
    public void onLogin() {
       //if (!validator.validate()) return;

        ProgressIndicator progressBar = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressBar.setMaxSize(36,  36);
        signInBtn.setText("");
        signInBtn.setGraphic(progressBar);
        signInBtn.setOnAction(null);

        Properties formData = new Properties();
        //formData.put("username", username.getText());
        //formData.put("password", password.getText());

        formData.put("username", "hqb203");
        formData.put("password", "123456789s");

        try{
            SocketClient socketClient = SocketClient.getInstance();
            Task waitResponse = new Task() {
                @Override
                protected Response call() throws Exception {
                    socketClient.sendRequest(
                            AuthRequest.builder()
                                    .action(Action.LOGIN)
                                    .formData(formData)
                                    .build());
                    return (Response) socketClient.getResponse();
                }
            };

            waitResponse.setOnSucceeded(e -> {
                AuthResponse res = (AuthResponse) waitResponse.getValue();
                if (res.getStatusCode() == StatusCode.AUTHENTICATED) {
  //                  authSocketService.cancel();
                    UserSocketService userSocketService = UserSocketService.getInstance(socketClient);
                    userSocketService.setLoggedUser(res.getUser());

                    Platform.runLater(() -> {
                        FXMLLoader loader = null;
                        if (res.getUser().getIsAdmin()){
                            loader = new FXMLLoader(Main.class.getResource("views/AdminView.fxml"));
                        }
                        else loader = new FXMLLoader(Main.class.getResource("views/UserView.fxml"));

                        try {
                            signUpBtn.getScene().setRoot(loader.load());
                        } catch (IOException err) {
                            throw new RuntimeException(err);
                        }
                    });
                }
                else {
                    Alert errAlert = new Alert(Alert.AlertType.ERROR);
                    errAlert.setTitle("Lỗi khi đăng nhập");
                    errAlert.setHeaderText(null);
                    errAlert.setContentText(res.getErr().getMessage());
                    errAlert.showAndWait();

                    signInBtn.setGraphic(null);
                    signInBtn.setText("Sign in");
                    signInBtn.setOnAction(e1 -> onLogin());
                }
            });

            Thread th = new Thread(waitResponse);
            th.setDaemon(true);
            th.start();
        }
        catch (IOException netError) {
            // Lỗi không kết nối được tới server
            Platform.runLater(() -> {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Connection Error!");
                error.setContentText("Không thể kết nối đến Server!!!");
                error.setHeaderText(null);
                error.showAndWait();

                signInBtn.setGraphic(null);
                signInBtn.setText("Sign in");
                signInBtn.setOnAction(e1 -> onLogin());
            });
        }
    }
}