package sample.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.Blend;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sample.enums.LawType;
import sample.utils.LawDistribution;

import javax.jws.WebParam;

public class SettingsController {
    private static Stage stage;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ComboBox<String> comboBoxFlowType;
    @FXML
    private TextField probabilityIn;
    @FXML
    private TextField probabilityCar;
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

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        SettingsController.stage = stage;
    }

    private InnerShadow getShadowEffect(Color color){
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(color);
        return innerShadow;
    }

    public void initialize(){
        comboBoxFlowType.getItems().add("Детерминированный");
        comboBoxFlowType.getItems().add("Случайный");
        comboBoxFlowType.getSelectionModel().select("Случайный");
        probabilityIn.setText("0.5");
        probabilityIn.focusedProperty().addListener((ov, oldV, newV) -> {
            if (!newV) { // focus lost
                try {
                    if (Double.parseDouble(probabilityIn.getText()) < 0 || Double.parseDouble(probabilityIn.getText()) > 1) {
                        probabilityIn.setEffect(getShadowEffect(Color.RED));
                    } else {
                        probabilityIn.setEffect(new Blend());
                    }
                }
                catch (Exception e){
                    probabilityIn.setEffect(getShadowEffect(Color.RED));
                }
            }
        });

        probabilityCar.setText("0.5");
        probabilityCar.focusedProperty().addListener((ov, oldV, newV) -> {
            if (!newV) { // focus lost
                try {
                    if (Double.parseDouble(probabilityCar.getText()) < 0 || Double.parseDouble(probabilityCar.getText()) > 1) {
                        probabilityCar.setEffect(getShadowEffect(Color.RED));
                    } else {
                        probabilityCar.setEffect(new Blend());
                    }
                }
                catch (Exception e){
                    probabilityCar.setEffect(getShadowEffect(Color.RED));
                }
            }
        });

        timeDetermine.getValueFactory().setValue(5);
        timeDetermine.setEditable(false);
        timeDetermine.setDisable(true);
        comboBoxLawTypeInterval.getItems().add("Равномерный");
        comboBoxLawTypeInterval.getItems().add("Нормальный");
        comboBoxLawTypeInterval.getItems().add("Показательный");
        comboBoxLawTypeInterval.getSelectionModel().select("Равномерный");
        param1Interval.getValueFactory().setValue(2);
        param1Interval.setEditable(false);
        param2Interval.getValueFactory().setValue(6);
        param2Interval.setEditable(false);
        comboBoxParkingType.getItems().add("Детерминированный");
        comboBoxParkingType.getItems().add("Случайный");
        comboBoxParkingType.getSelectionModel().select("Детерминированный");

        timeParkDetermine.getValueFactory().setValue(1);
        timeParkDetermine.setEditable(true);
        timeParkDetermine.focusedProperty().addListener((ov, oldV, newV) -> {
            if (!newV) { // focus lost
                timeParkDetermine.increment(0);
                try {
                    if (timeParkDetermine.getValue() < 1 || timeParkDetermine.getValue() > 180) {
                        timeParkDetermine.setEffect(getShadowEffect(Color.RED));
                    } else {
                        timeParkDetermine.setEffect(new Blend());
                    }
                }
                catch (Exception e){
                    timeParkDetermine.setEffect(getShadowEffect(Color.RED));
                }
            }
        });

        comboBoxLawTypePark.getItems().add("Равномерный");
        comboBoxLawTypePark.getItems().add("Нормальный");
        comboBoxLawTypePark.getItems().add("Показательный");
        comboBoxLawTypePark.getSelectionModel().select("Нормальный");
        comboBoxLawTypePark.setDisable(true);

        param1Park.getValueFactory().setValue(15);
        param1Park.focusedProperty().addListener((ov, oldV, newV) -> {
            if (!newV) { // focus lost
                param1Park.increment(0);
                try {
                    if (param1Park.getValue() < 1 || param1Park.getValue() > 180) {
                        param1Park.setEffect(getShadowEffect(Color.RED));
                    } else {
                        param1Park.setEffect(new Blend());
                    }
                }
                catch (Exception e){
                    param1Park.setEffect(getShadowEffect(Color.RED));
                }
            }
        });

        param2Park.getValueFactory().setValue(2);
        param2Park.setEditable(false);
        param1Park.setDisable(true);
        param2Park.setDisable(true);

        costCarDay.setText("30");
        costCarDay.focusedProperty().addListener((ov, oldV, newV) -> {
            if (!newV) { // focus lost
                try {
                    if (Integer.parseInt(costCarDay.getText()) < 0) {
                        costCarDay.setEffect(getShadowEffect(Color.RED));
                    } else {
                        costCarDay.setEffect(new Blend());
                    }
                }
                catch (Exception e){
                    costCarDay.setEffect(getShadowEffect(Color.RED));
                }
            }
        });

        costCarNight.setText("40");
        costCarNight.focusedProperty().addListener((ov, oldV, newV) -> {
            if (!newV) { // focus lost
                try {
                    if (Integer.parseInt(costCarNight.getText()) < 0) {
                        costCarNight.setEffect(getShadowEffect(Color.RED));
                    } else {
                        costCarNight.setEffect(new Blend());
                    }
                }
                catch (Exception e){
                    costCarNight.setEffect(getShadowEffect(Color.RED));
                }
            }
        });

        costTruckDay.setText("40");
        costTruckDay.focusedProperty().addListener((ov, oldV, newV) -> {
            if (!newV) { // focus lost
                try {
                    if (Integer.parseInt(costTruckDay.getText()) < 0) {
                        costTruckDay.setEffect(getShadowEffect(Color.RED));
                    } else {
                        costTruckDay.setEffect(new Blend());
                    }
                }
                catch (Exception e){
                    costTruckDay.setEffect(getShadowEffect(Color.RED));
                }
            }
        });

        costTruckNight.setText("50");
        costTruckNight.focusedProperty().addListener((ov, oldV, newV) -> {
            if (!newV) { // focus lost
                try {
                    if (Integer.parseInt(costTruckNight.getText()) < 0) {
                        costTruckNight.setEffect(getShadowEffect(Color.RED));
                    } else {
                        costTruckNight.setEffect(new Blend());
                    }
                }
                catch (Exception e){
                    costTruckNight.setEffect(getShadowEffect(Color.RED));
                }
            }
        });

        timeStartModelling.getValueFactory().setValue(12);
        timeStartModelling.setEditable(false);
    }

    @FXML
    private void changeFlowType(){
        if(comboBoxFlowType.getSelectionModel().getSelectedItem().toString().equals("Детерминированный")){
            comboBoxLawTypeInterval.setDisable(true);
            param1Interval.setDisable(true);
            param2Interval.setDisable(true);
            timeDetermine.setDisable(false);
        }
        else{
            comboBoxLawTypeInterval.setDisable(false);
            param1Interval.setDisable(false);
            param2Interval.setDisable(false);
            timeDetermine.setDisable(true);
        }
    }

    @FXML
    private void changeFlowLawType(){
        if(comboBoxLawTypeInterval.getSelectionModel().getSelectedItem().equals("Равномерный")){
            labelParam1Interval.setText("Ниж.граница:");
            labelParam2Interval.setText("Верх.граница:");
            param2Interval.setDisable(false);
        }
        else if(comboBoxLawTypeInterval.getSelectionModel().getSelectedItem().equals("Нормальный")){
            labelParam1Interval.setText("Мат.ожидание:");
            labelParam2Interval.setText("Дисперсия:");
            param2Interval.setDisable(false);
        }
        else {
            labelParam1Interval.setText("Интенсивность:");
            param2Interval.setDisable(true);
        }
    }

    @FXML
    private void changeParkingType(){
        if(comboBoxParkingType.getSelectionModel().getSelectedItem().toString().equals("Детерминированный")){
            comboBoxLawTypePark.setDisable(true);
            param1Park.setDisable(true);
            param2Park.setDisable(true);
            timeParkDetermine.setDisable(false);
        }
        else{
            comboBoxLawTypePark.setDisable(false);
            param1Park.setDisable(false);
            param2Park.setDisable(false);
            timeParkDetermine.setDisable(true);
        }
    }

    @FXML
    private void changeParkLawType(){
        if(comboBoxLawTypePark.getSelectionModel().getSelectedItem().equals("Равномерный")){
            labelParam1Park.setText("Ниж.граница:");
            labelParam2Park.setText("Верх.граница:");
            param2Park.setDisable(false);
        }
        else if(comboBoxLawTypeInterval.getSelectionModel().getSelectedItem().equals("Нормальный")){
            labelParam1Park.setText("Мат.ожидание:");
            labelParam2Park.setText("Дисперсия:");
            param2Park.setDisable(false);
        }
        else {
            labelParam1Park.setText("Интенсивность:");
            param2Park.setDisable(true);
        }
    }

    @FXML
    private void confirm(){
        boolean isCorrect = true;
        for(Node node: anchorPane.getChildren()){
            if(node.getEffect() instanceof InnerShadow){
                isCorrect = false;
                break;
            }
        }
        if(isCorrect) {
            LawType lawTypeFlow = null;
            LawDistribution flowLawDistribution = null;
            if (comboBoxFlowType.getSelectionModel().getSelectedItem().equals("Детерминированный")) {
                lawTypeFlow = LawType.DETERMINE;
                flowLawDistribution = new LawDistribution(lawTypeFlow, timeDetermine.getValue(), LawDistribution.NO_PARAMETER);
            } else {
                switch (comboBoxLawTypeInterval.getSelectionModel().getSelectedItem()) {
                    case "Равномерный": {
                        lawTypeFlow = LawType.UNIFORM;
                        break;
                    }
                    case "Нормальный": {
                        lawTypeFlow = LawType.NORMAL;
                        break;
                    }
                    case "Показательный": {
                        lawTypeFlow = LawType.EXPONENTIAL;
                        break;
                    }
                }
                flowLawDistribution = new LawDistribution(lawTypeFlow, param1Interval.getValue(), param2Interval.getValue());
            }
            LawType lawTypeStay = null;
            LawDistribution lawDistributionStay = null;
            if (comboBoxParkingType.getSelectionModel().getSelectedItem().equals("Детерминированный")) {
                lawTypeStay = LawType.DETERMINE;
                lawDistributionStay = new LawDistribution(lawTypeStay, timeParkDetermine.getValue(), LawDistribution.NO_PARAMETER);
            } else {
                switch (comboBoxLawTypePark.getSelectionModel().getSelectedItem()) {
                    case "Равномерный": {
                        lawTypeStay = LawType.UNIFORM;
                        break;
                    }
                    case "Нормальный": {
                        lawTypeStay = LawType.NORMAL;
                        break;
                    }
                    case "Показательный": {
                        lawTypeStay = LawType.EXPONENTIAL;
                        break;
                    }
                }
                lawDistributionStay = new LawDistribution(lawTypeStay, param1Park.getValue(), param2Park.getValue());
            }
                try {
                    ModellingController.setFlowLawDistribution(flowLawDistribution);
                    ModellingController.setStayLawDistribution(lawDistributionStay);
                    ModellingController.setProbabilityCar(Double.parseDouble(probabilityCar.getText()));
                    ModellingController.setProbabilityIn(Double.parseDouble(probabilityIn.getText()));
                    int[] tariffs = new int[]{Integer.parseInt(costCarDay.getText()), Integer.parseInt(costCarNight.getText()), Integer.parseInt(costTruckDay.getText()), Integer.parseInt(costTruckNight.getText())};
                    ModellingController.setTariffs(tariffs);
                    ModellingController.setHoursStart(timeStartModelling.getValue());
                    ModellingController.setIsParameterized(true);
                    stage.close();
                } catch (Exception e) {
                    Alert alertWindow = new Alert(Alert.AlertType.ERROR);
                    alertWindow.setTitle("Ошибка");
                    alertWindow.setHeaderText(null);
                    alertWindow.setContentText("Ошибка задания параметров. Проверьте корректность введенных данных.");
                    alertWindow.showAndWait();
                }
        }
        else {
            Alert alertWindow = new Alert(Alert.AlertType.ERROR);
            alertWindow.setTitle("Ошибка");
            alertWindow.setHeaderText(null);
            alertWindow.setContentText("Ошибка задания параметров. Проверьте корректность введенных данных.");
            alertWindow.showAndWait();
        }
    }

    @FXML
    private void cancel(){
        stage.close();
    }
}
