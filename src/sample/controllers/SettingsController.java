package sample.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SettingsController {
    @FXML
    private ComboBox<String> comboBoxFlowType;
    @FXML
    private TextField probabilityIn;
    @FXML
    private TextField probabilityCar;
    @FXML
    private TextField probabilityTruck;
    @FXML
    private Spinner<Integer> timeDetermine;
    @FXML
    private ComboBox<String> comboBoxLawTypeInterval;
    @FXML
    private Label labelParam1Interval;
    @FXML
    private Label labelParam2Interval;
    @FXML
    private Spinner<Integer> param1Interval;
    @FXML
    private Spinner<Integer> param2Interval;
    @FXML
    private ComboBox<String> comboBoxParkingType;
    @FXML
    private Spinner<Integer> timeParkDetermine;
    @FXML
    private ComboBox<String> comboBoxLawTypePark;
    @FXML
    private Label labelParam1Park;
    @FXML
    private Label labelParam2Park;
    @FXML
    private Spinner<Integer> param1Park;
    @FXML
    private Spinner<Integer> param2Park;
    @FXML
    private TextField costCarDay;
    @FXML
    private TextField costCarNight;
    @FXML
    private TextField costTruckDay;
    @FXML
    private TextField costTruckNight;
    @FXML
    private Spinner<Integer> timeStartModelling;
    @FXML
    private Button buttonConfirm;
    @FXML
    private Button buttonCancel;

    public void initialize(){
        comboBoxFlowType.getItems().add("Детерминированный");
        comboBoxFlowType.getItems().add("Случайный");
        comboBoxFlowType.getSelectionModel().select("Детерминированный");
        probabilityIn.setText("0.1");
        probabilityCar.setText("0.5");
        probabilityTruck.setText("0.5");
        timeDetermine.getValueFactory().setValue(5);
        comboBoxLawTypeInterval.getItems().add("Равномерный");
        comboBoxLawTypeInterval.getItems().add("Нормальный");
        comboBoxLawTypeInterval.getItems().add("Показательный");
        comboBoxLawTypeInterval.getSelectionModel().select("Равномерный");
        param1Interval.getValueFactory().setValue(1);
        param2Interval.getValueFactory().setValue(10);
        comboBoxParkingType.getItems().add("Детерминированный");
        comboBoxParkingType.getItems().add("Случайный");
        timeParkDetermine.getValueFactory().setValue(15);
        comboBoxLawTypePark.getItems().add("Равномерный");
        comboBoxLawTypePark.getItems().add("Нормальный");
        comboBoxLawTypePark.getItems().add("Показательный");
        comboBoxLawTypePark.getSelectionModel().select("Равномерный");
        param1Park.getValueFactory().setValue(15);
        param2Park.getValueFactory().setValue(30);
        costCarDay.setText("10");
        costCarNight.setText("5");
        costTruckDay.setText("15");
        costTruckNight.setText("8");
    }
}
