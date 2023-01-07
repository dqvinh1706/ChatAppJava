package com.chatapp.client.controllers;

import com.chatapp.client.Main;
import com.chatapp.client.workers.UserSocketService;
import com.chatapp.commons.enums.Action;
import com.chatapp.commons.models.LoginHistory;
import com.chatapp.commons.request.ManageUsersRequest;
import com.chatapp.commons.response.LoginListRespone;
import com.chatapp.commons.response.Response;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class AdminLoginListController implements Initializable {
    private final ObservableList<LoginHistoryClone> data = FXCollections.observableArrayList();
    private final UserSocketService userSocketService = UserSocketService.getInstance();
    @FXML
    private AnchorPane scenePane;
    @FXML
    private TableView LoginList = new TableView();
    @FXML
    void turnBackAdminView(ActionEvent event){
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/AdminView.fxml"));
        try {
            scenePane.getScene().setRoot(loader.load());
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }

    private String Date2String(Date a){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormat.format(a);
    }

    private void getData(){
        if(!userSocketService.isRunning()) userSocketService.start();
        Task waitResponse = new Task() {
            @Override
            protected Response call() throws Exception {
                userSocketService.addRequest(
                        ManageUsersRequest.builder()
                                .action(Action.GET_LOGIN_LIST)
                                .build()
                );
                return (Response) userSocketService.getResponse();
            }
        };

        waitResponse.setOnSucceeded(e -> {
            LoginListRespone res = (LoginListRespone) waitResponse.getValue();
            List<LoginHistory> loginList =  res.getLoginList();
            for(LoginHistory loginHistory: loginList){
                String createdAt = Date2String(loginHistory.getCreatedAt());
                data.add(new AdminLoginListController.LoginHistoryClone(
                        loginHistory.getId(),
                        loginHistory.getUsername(),
                        createdAt
                ));
            }
        });

        Thread th = new Thread(waitResponse);
        th.setDaemon(true);
        th.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        this.getData();

        TableColumn<LoginHistoryClone, String> userIDColumn = new TableColumn<LoginHistoryClone, String>("User ID");
        TableColumn<LoginHistoryClone, String> userNameColumn = new TableColumn<LoginHistoryClone, String>("Username");
        TableColumn<LoginHistoryClone, String> loginAtColumn = new TableColumn<LoginHistoryClone, String>("Login at");

        userIDColumn.setCellValueFactory(new PropertyValueFactory<LoginHistoryClone, String>("id"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<LoginHistoryClone, String>("username"));
        loginAtColumn.setCellValueFactory(new PropertyValueFactory<LoginHistoryClone, String>("createdAt"));

        userIDColumn.setMinWidth(160.0);
        userNameColumn.setMinWidth(160.0);
        loginAtColumn.setMinWidth(160.0);

        LoginList.setItems(data);
        LoginList.getColumns().addAll(userIDColumn, userNameColumn, loginAtColumn);
        LoginList.setEditable(false);

        LoginList.setOnMouseClicked(onClickedEvent());
        LoginList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection)->{
            if (newSelection != null) {
                LoginList.getSelectionModel().clearSelection();
            }
        });
    }

    private EventHandler<MouseEvent> onClickedEvent() {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    while (scenePane.getChildren().size() > 2) {
                        scenePane.getChildren().remove(scenePane.getChildren().size() - 1);
                    }
                    ListView<String> options = new ListView<>();
                    options.getItems().addAll("Add", "Delete", "Lock", "Update password", "Show login history", "Show friend list");

                    options.setMaxHeight(100);

                    options.setLayoutX(mouseEvent.getSceneX());
                    options.setLayoutY(mouseEvent.getSceneY());
                    scenePane.getChildren().add(options);
                } else if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    while (scenePane.getChildren().size() > 2) {
                        scenePane.getChildren().remove(scenePane.getChildren().size() - 1);
                    }
                }
            }
        };
    }

    public static class LoginHistoryClone{
        public SimpleIntegerProperty id;
        public SimpleStringProperty username;
        public SimpleStringProperty createdAt;

        private Date String2Date(String date) throws Exception{
            return new SimpleDateFormat("dd/MM/yyyy").parse(date);
        }

        public LoginHistoryClone(int ID, String username, String createdAt){
            this.id = new SimpleIntegerProperty(ID);
            this.username = new SimpleStringProperty(username);
            this.createdAt = new SimpleStringProperty(createdAt);
        }

        public int getId() {
            return id.get();
        }

        public String getUsername() {
            return username.get();
        }

        public String getCreatedAt() {
            return createdAt.get();
        }
    }
}
