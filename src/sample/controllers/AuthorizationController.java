package sample.controllers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


public class AuthorizationController {
    private static Stage stage;
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private TextField textBoxLogin;
    @FXML
    private PasswordField textBoxPassword;
    @FXML
    private Button buttonEntry;
    @FXML
    private Button buttonExit;
    @FXML
    private Label chooseRole;
    @FXML
    private Label login;
    @FXML
    private Label password;

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        AuthorizationController.stage = stage;
    }

    @FXML
    private void initialize(){
        /*textBoxLogin.setText("admin");
        textBoxPassword.setText("park1ng");*/
        comboBox.getItems().add("Администратор");
        comboBox.getItems().add("Пользователь");
        comboBox.getSelectionModel().select("Пользователь");
        textBoxLogin.setDisable(true);
        textBoxPassword.setDisable(true);
    }

    @FXML
    private void clickEntry(){
        String admLogin = "admin";
        String admPassword = "park1ng";
        if(comboBox.getSelectionModel().getSelectedItem().equals("Пользователь")){
            try {
                toModelling();
            }
            catch (IOException ex){
                ex.printStackTrace();
                showErrorWindow("Ошибка загрузки. ");
            }
        }
        else {
            String login = textBoxLogin.getText();
            String password = textBoxPassword.getText();
            try {
                if (login.equals(admLogin) && password.equals(admPassword)) {
                    toConstruction();
                    ConstructionController.setStage(stage);
                } else {
                    showErrorWindow("Неверно введен логин и/или пароль. Повторите ввод заново. ");
                }
            }
            /*try (FileInputStream fileInputStream = new FileInputStream("entryData.bin")) {
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                String[] entryData = ((String) objectInputStream.readObject()).split("_");
                if (entryData[0].equals(login) && entryData[1].equals(password)) {
                    toConstruction();
                    ConstructionController.setStage(stage);
                }
                else {
                    showErrorWindow("Неверно введен логин и/или пароль. Повторите ввод заново. ");
                }
            }*/
            catch (Exception ex) {
                ex.printStackTrace();
                //showErrorWindow(ex.getMessage());
            }
        }
    }

    @FXML
    private void clickCancel(){
        stage.close();
    }
    @FXML
    private void changeElement(){
        if(comboBox.getSelectionModel().getSelectedItem().toString().equals("Пользователь")){
            textBoxLogin.setDisable(true);
            textBoxPassword.setDisable(true);
        }
        else{
            textBoxLogin.setDisable(false);
            textBoxPassword.setDisable(false);
        }
    }

    private void toConstruction() throws IOException{
        Parent constructionWindow = FXMLLoader.load(getClass().getResource("../fxmls/construction.fxml"));
        stage.setTitle("Конструирование");
        stage.setScene(new Scene(constructionWindow, 600, 400));
        stage.show();
    }

    private void toModelling() throws IOException{
        Parent modellingWindow = FXMLLoader.load(getClass().getResource("../fxmls/modelling.fxml"));
        stage.setTitle("Моделирование");
        stage.setScene(new Scene(modellingWindow, 600, 400));
        stage.show();
    }

    private void showErrorWindow(String message){
        Alert alertWindow = new Alert(Alert.AlertType.ERROR);
        alertWindow.setTitle("Ошибка");
        alertWindow.setHeaderText(null);
        alertWindow.setContentText(message);
        alertWindow.showAndWait();
    }
}
