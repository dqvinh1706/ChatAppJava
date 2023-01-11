package com.chatapp.client.controllers;

import com.chatapp.client.Main;
import com.chatapp.client.workers.UserSocketService;
import com.chatapp.commons.enums.Action;
import com.chatapp.commons.models.User;
import com.chatapp.commons.request.ManageUsersRequest;
import com.chatapp.commons.response.ActionResponse;
import com.chatapp.commons.response.Response;
import com.chatapp.commons.utils.TimestampUtil;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.time.ZoneId;
import java.util.Date;

public class AdminAddNewAccountController implements Initializable {
    private final UserSocketService userSocketService = UserSocketService.getInstance();
    @FXML
    private TextField nameInput;
    @FXML
    private TextField userNameInput;
    @FXML
    private TextField passwordInput;
    @FXML
    private TextField addressInput;
    @FXML
    private TextField emailInput;
    @FXML
    private DatePicker dobInput;
    @FXML
    private CheckBox isMale;
    @FXML
    private CheckBox isFemale;
    @FXML
    private Label ResultText;

    @FXML
    private AnchorPane scenePane;

    @FXML
    private void backToAdminUserManagement(){
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/AdminUserManagerView.fxml"));
        try {
            scenePane.getScene().setRoot(loader.load());
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }

    @FXML
    private void submitClick(){
        String username = userNameInput.getText();
        if (username.length() > 50 || username.length() < 5) {
            ResultText.setText("Can't input empty username or username with length over 50 characters.");
            return;
        }
        String password = passwordInput.getText();
        if (password.length() > 255 || password.length() < 8) {
            ResultText.setText("Can't input empty password or password with length over 255 characters.");
            return;
        }

        String name = nameInput.getText();

        String email = emailInput.getText();
        String address = addressInput.getText();
        LocalDate dob = LocalDate.now();
        dob = dobInput.getValue();
        String gender = "";
        if (isMale.isSelected()) gender = "nam";
        else if (isFemale.isSelected()) gender = "nu";

        if(!userSocketService.isRunning()) userSocketService.start();

        User newUser = new User(
                username,
                password,
                name,
                address,
                email,
                gender,
                Date.from(dob.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                TimestampUtil.getCurrentTime(),
                TimestampUtil.getCurrentTime()
        );

        Task waitResponse = new Task() {
            @Override
            protected Response call() throws Exception {
                userSocketService.addRequest(
                        ManageUsersRequest.builder()
                                .action(Action.ADD_NEW_USER)
                                .body(newUser)
                                .build()
                );
                return (Response) userSocketService.getResponse();
            }
        };

        waitResponse.setOnSucceeded(e -> {
            ActionResponse res = (ActionResponse) waitResponse.getValue();
            ResultText.setText(res.getNotification());
        });

        Thread th = new Thread(waitResponse);
        th.setDaemon(true);
        th.start();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String pattern = "dd/MM/yyyy";
        FxDatePickerConverter converter = new FxDatePickerConverter(pattern);

        dobInput.setConverter(converter);
        dobInput.setPromptText(pattern.toLowerCase());
        isMale.setOnAction(event->{if (isFemale.isSelected()) isFemale.setSelected(false);});
        isFemale.setOnAction(event->{if (isMale.isSelected()) isMale.setSelected(false);});
    }
    private static class FxDatePickerConverter extends StringConverter<LocalDate> {
        private String pattern = "MM/dd/yyyy";
        private DateTimeFormatter dtFormatter;
        public FxDatePickerConverter() {
            dtFormatter = DateTimeFormatter.ofPattern(pattern);
        }
        public FxDatePickerConverter(String pattern) {
            this.pattern = pattern;
            dtFormatter = DateTimeFormatter.ofPattern(pattern);
        }
        // Change String to LocalDate
        public LocalDate fromString(String text) {
            LocalDate date = null;
            if (text != null && !text.trim().isEmpty()) date = LocalDate.parse(text, dtFormatter);
            return date;
        }
        // Change LocalDate to String
        public String toString(LocalDate date) {
            String text = null;
            if (date != null) text = dtFormatter.format(date);
            return text;
        }
    }
}


