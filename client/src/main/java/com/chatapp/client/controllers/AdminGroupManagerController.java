package com.chatapp.client.controllers;

import com.chatapp.client.Main;
import com.chatapp.client.workers.UserSocketService;
import com.chatapp.commons.enums.Action;
import com.chatapp.commons.models.Group;
import com.chatapp.commons.models.User;
import com.chatapp.commons.request.ManageUsersRequest;
import com.chatapp.commons.response.*;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminGroupManagerController implements Initializable {
    private final ObservableList<GroupClone> data = FXCollections.observableArrayList();
    private final UserSocketService userSocketService = UserSocketService.getInstance();
    private int SelectedID = -1;
    @FXML
    private AnchorPane scenePane;
    @FXML
    private TableView<GroupClone> groupsTable = new TableView<>();

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
        if (a == null) return "";
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(a);
    }
    private void getData(){
        if(!userSocketService.isRunning()) userSocketService.start();

        Task waitResponse = new Task() {
            @Override
            protected Response call() throws Exception {
                userSocketService.addRequest(
                        ManageUsersRequest.builder()
                                .action(Action.GET_ALL_GROUPS)
                                .build()
                );
                return (Response) userSocketService.getResponse();
            }
        };

        waitResponse.setOnSucceeded(e -> {
            AllGroupsResponse res = (AllGroupsResponse) waitResponse.getValue();
            List<Group> groupsList =  res.getAllGroups();
            if (groupsList != null) {
                for (Group group : groupsList) {
                    String createdDate = Date2String(group.getCreatedAt());
                    data.add(new GroupClone(
                            group.getId(),
                            group.getName(),
                            createdDate
                    ));
                }
            }
        });
        Thread th = new Thread(waitResponse);
        th.setDaemon(true);
        th.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        this.getData();

        TableColumn<GroupClone, Integer> idColumn = new TableColumn<GroupClone, Integer>("ID");
        TableColumn<GroupClone, String> nameColumn = new TableColumn<GroupClone, String>("Name");
        TableColumn<GroupClone, String> createdColumn = new TableColumn<GroupClone, String>("Created Date");


        idColumn.setCellValueFactory(new PropertyValueFactory<GroupClone, Integer>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<GroupClone, String>("name"));
        createdColumn.setCellValueFactory(new PropertyValueFactory<GroupClone, String>("createdDate"));


        idColumn.setMinWidth(320.0);
        nameColumn.setMinWidth(320.0);
        createdColumn.setMinWidth(320.0);

        groupsTable.setItems(data);
        groupsTable.getColumns().addAll(idColumn, nameColumn, createdColumn);
        groupsTable.setEditable(false);

        groupsTable.setOnMouseClicked(onClickedEvent());
        groupsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection)->{
            if (newSelection != null) {
                GroupClone user = groupsTable.getSelectionModel().getSelectedItem();
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
                    options.getItems().addAll("Show Admin", "Show Member");

                    options.setMaxHeight(100);

                    options.setLayoutX(mouseEvent.getSceneX());
                    options.setLayoutY(mouseEvent.getSceneY());

                    options.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                            System.out.println(newValue + " id= " + SelectedID);
                            if (newValue.equals("Show Admin")){
                                try {
                                    FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/ShowGroupAdminList.fxml"));
                                    Parent root = (Parent) loader.load();

                                    ShowGroupAdminListController showGroupAdminListController = loader.getController();
                                    showGroupAdminListController.setValue(SelectedID);

                                    Stage stage = new Stage();
                                    stage.setTitle("");
                                    stage.setScene(new Scene(root));
                                    stage.show();
                                }
                                catch (IOException e){
                                    e.printStackTrace();
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
    public static class GroupClone{
        public SimpleIntegerProperty id;
        public SimpleStringProperty name;
        public SimpleStringProperty createdDate;

        public GroupClone(int ID, String Name, String createdDate){
            this.id = new SimpleIntegerProperty(ID);
            this.name = new SimpleStringProperty(Name);
            this.createdDate = new SimpleStringProperty(createdDate);
        }

        public int getId() {
            return id.get();
        }
        
        public String getName() {
            return name.get();
        }

        public String getCreatedDate() {
            return createdDate.get();
        }
    }
}



