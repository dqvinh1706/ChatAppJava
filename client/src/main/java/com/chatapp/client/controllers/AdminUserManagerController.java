package com.chatapp.client.controllers;

import com.chatapp.client.Main;
import com.chatapp.client.workers.UserSocketService;
import com.chatapp.commons.enums.Action;
import com.chatapp.commons.models.User;
import com.chatapp.commons.request.ManageUsersRequest;
import com.chatapp.commons.response.AllUsersResponse;
import com.chatapp.commons.response.Response;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class AdminUserManagerController implements Initializable {
    private final ObservableList<UserClone> data = FXCollections.observableArrayList();
    private final UserSocketService userSocketService = UserSocketService.getInstance();
    @FXML
    private AnchorPane scenePane;
    @FXML
    private TableView<UserClone> usersTable = new TableView<>();
    @FXML
    void turnBackAdminView(ActionEvent event){
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/AdminView.fxml"));
        try {
            scenePane.getScene().setRoot(loader.load());
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }

    private int SelectedID = -1;
    private String Date2String(Date a){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(a);
    }
    private void getData(){
            if(!userSocketService.isRunning()) userSocketService.start();
            Task waitResponse = new Task() {
                @Override
                protected Response call() throws Exception {
                    userSocketService.addRequest(
                            ManageUsersRequest.builder()
                                    .action(Action.GET_ALL_USERS)
                                    .build()
                    );
                    return (Response) userSocketService.getResponse();
                }
            };

            waitResponse.setOnSucceeded(e -> {
                AllUsersResponse res = (AllUsersResponse) waitResponse.getValue();
                List<User> usersList =  res.getAllUsers();
                for(User user: usersList){
                    String BD = Date2String(user.getDOB());
                    data.add(new UserClone(
                            user.getId(),
                            user.getUsername(),
                            user.getFullName(),
                            user.getAddress(),
                            BD,
                            false,
                            user.getEmail()
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

        TableColumn<UserClone, String> userNameColumn = new TableColumn<UserClone, String>("User Name");
        TableColumn<UserClone, String> nameColumn = new TableColumn<UserClone, String>("Name");
        TableColumn<UserClone, String> addressColumn = new TableColumn<UserClone, String>("Address");
        TableColumn<UserClone, String> DOBColumn = new TableColumn<UserClone, String>("Birthday");
        TableColumn<UserClone, Boolean> genderColumn = new TableColumn<UserClone, Boolean>("Gender");
        TableColumn<UserClone, String> emailColumn = new TableColumn<UserClone, String>("Email");

        userNameColumn.setCellValueFactory(new PropertyValueFactory<UserClone, String>("username"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<UserClone, String>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<UserClone, String>("address"));
        DOBColumn.setCellValueFactory(new PropertyValueFactory<UserClone, String>("birthday"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<UserClone, Boolean>("gender"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<UserClone, String>("email"));

        userNameColumn.setMinWidth(160.0);
        nameColumn.setMinWidth(160.0);
        addressColumn.setMinWidth(160.0);
        DOBColumn.setMinWidth(160.0);
        genderColumn.setMinWidth(160.0);
        emailColumn.setMinWidth(160.0);

        usersTable.setItems(data);
        usersTable.getColumns().addAll(userNameColumn, nameColumn, addressColumn, DOBColumn, genderColumn, emailColumn);
        usersTable.setEditable(false);

        usersTable.setOnMouseClicked(onClickedEvent());
        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection)->{
            if (newSelection != null) {
                UserClone user = usersTable.getSelectionModel().getSelectedItem();
                this.SelectedID = user.getId();
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

                    options.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                            System.out.println(newValue + " id= " + SelectedID);
                            if (newValue.equals("Add")){
                                FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/AdminAddNewAccount.fxml"));
                                try {
                                    scenePane.getScene().setRoot(loader.load());
                                } catch (IOException err) {
                                    throw new RuntimeException(err);
                                }
                            }

                        }
                    });
                    scenePane.getChildren().add(options);
                }
                else if (mouseEvent.getButton() == MouseButton.PRIMARY){
                    while (scenePane.getChildren().size() > 2) {
                        scenePane.getChildren().remove(scenePane.getChildren().size() - 1);
                    }
                }
            }
        };
    }

    public static class UserClone{
        public SimpleIntegerProperty id;
        public SimpleStringProperty username;
        public SimpleStringProperty name;
        public SimpleStringProperty birthday;
        public SimpleStringProperty address;
        public SimpleBooleanProperty gender;
        public SimpleStringProperty email;

        public UserClone(int ID, String username, String Name, String address, String birthday, Boolean gender, String email){
            this.id = new SimpleIntegerProperty(ID);
            this.username = new SimpleStringProperty(username);
            this.name = new SimpleStringProperty(Name);
            this.address = new SimpleStringProperty(address);
            this.birthday = new SimpleStringProperty(birthday);
            this.gender = new SimpleBooleanProperty(gender);
            this.email = new SimpleStringProperty(email);
        }

        public int getId() {
            return id.get();
        }

        public String getUsername() {
            return username.get();
        }

        public String getName() {
            return name.get();
        }

        public String getBirthday() {
            return birthday.get();
        }

        public String getAddress() {
            return address.get();
        }
        public Boolean getGender() {
            return gender.get();
        }

        public String getEmail() {
            return email.get();
        }
    }
}



