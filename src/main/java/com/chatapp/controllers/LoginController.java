package com.chatapp.controllers;

import com.chatapp.Main;
import com.chatapp.StoreContext;
import com.chatapp.components.Avatar.Avatar;
import com.chatapp.components.CustomPasswordField.CustomPasswordField;
import com.chatapp.components.CustomTextField.CustomTextField;
import com.chatapp.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import net.synedra.validatorfx.Validator;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private VBox loginTab;

    private CustomTextField username;
    private CustomPasswordField password;

    @FXML
    private Button signUpBtn;

    private Validator validator;

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

        VBox.setMargin(username, new Insets(0, 0, 10, 0));
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
    private void onLogin() {
        StoreContext store = StoreContext.getInstance();
        store.getMainUser().set(new User(username.getTextProperty().get(), password.getTextProperty().get()));
    }
}
