package sample.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import javafx.scene.control.Label;
import sample.utils.LawDistribution;
import sample.models.Parking;
import sample.models.ParkingCell;
import sample.threads.*;
import sample.enums.*;

public class ModellingController {
    private static Stage stage;
    private static Parking parking;
    @FXML
    private ImageView play;
    @FXML
    private ImageView pause;
    @FXML
    private ImageView stop;
    @FXML
    private ImageView slow;
    @FXML
    private ImageView fast;
    @FXML
    private Label clock;
    @FXML
    private Label countNoFreePlaces;
    @FXML
    private Label countPlaces;
    @FXML
    private Label profit;
    @FXML
    private Label averagePlaces;
    @FXML
    private GridPane gridPane;
    @FXML
    private AnchorPane anchorPaneFlow;
    @FXML
    private Menu menuSettings;

    private static volatile Calendar calendar;
    private static volatile Booster booster;

    private FlowThread flowThread;
    private TimeThread timeThread;

    private boolean isStopped;
    private boolean isPaused;
    private boolean isLoaded;
    private static boolean isParameterized;

    private static LawDistribution flowLawDistribution;
    private static LawDistribution stayLawDistribution;
    private static int[] tariffs;
    private static int cashBox;
    private static int countCars;

    private static double probabilityCar;
    private static double probabilityIn;
    private static int hoursStart;

    public ModellingController(Parking parking){
        ModellingController.parking = parking;
    }

    public ModellingController(){
        parking = null;
    }

    public static Calendar getCalendar() {
        return calendar;
    }


    public static Booster getBooster() {
        return booster;
    }


    public static LawDistribution getFlowLawDistribution() {
        return flowLawDistribution;
    }


    public static LawDistribution getStayLawDistribution() {
        return stayLawDistribution;
    }


    public static int[] getTariffs() {
        return tariffs;
    }


    public static double getProbabilityCar() {
        return probabilityCar;
    }


    public static double getProbabilityIn() {
        return probabilityIn;
    }

    public static void setStage(Stage stage) {
        ModellingController.stage = stage;
    }

    public static void setFlowLawDistribution(LawDistribution flowLawDistribution) {
        ModellingController.flowLawDistribution = flowLawDistribution;
    }

    public static void setStayLawDistribution(LawDistribution stayLawDistribution) {
        ModellingController.stayLawDistribution = stayLawDistribution;
    }

    public static void setTariffs(int[] tariffs) {
        ModellingController.tariffs = tariffs;
    }

    public static void setProbabilityCar(double probabilityCar) {
        ModellingController.probabilityCar = probabilityCar;
    }

    public static void setProbabilityIn(double probabilityIn) {
        ModellingController.probabilityIn = probabilityIn;
    }

    public static int getHoursStart() {
        return hoursStart;
    }

    public static void setHoursStart(int hoursStart) {
        ModellingController.hoursStart = hoursStart;
    }

    public static boolean isIsParameterized() {
        return isParameterized;
    }

    public static void setIsParameterized(boolean isParameterized) {
        ModellingController.isParameterized = isParameterized;
    }

    public static int getCountCars() {
        return countCars;
    }

    public static void setCountCars(int countCars) {
        ModellingController.countCars = countCars;
    }

    public void initialize(){
        play.setDisable(true);
        pause.setDisable(true);
        stop.setDisable(true);
        slow.setDisable(true);
        fast.setDisable(true);
        calendar = new GregorianCalendar();
        booster = Booster.BOOSTER_DEFAULT;
        cashBox = 0;
        if(parking == null) {
            parking = new Parking(5, 5);
            drawParkingInit();
        }
        else {
            Parking tempParking = parking;
            int difHor = Math.abs(5 - parking.getHorizontalSize());
            int difVer = Math.abs(5 - parking.getVerticalSize());
            if (5 < parking.getHorizontalSize()) {
                for (int i = 0; i < difHor; i++) {
                    changeHorizontalSize();
                }
            }
            if (5 > parking.getHorizontalSize()) {
                for (int i = 0; i < difHor; i++) {
                    changeHorizontalSize();
                }
            }
            if (5 < parking.getVerticalSize()) {
                for (int i = 0; i < difVer; i++) {
                    changeVerticalSize();
                }
            }
            if (5 > parking.getVerticalSize()) {
                for (int i = 0; i < difVer; i++) {
                    changeVerticalSize();
                }
            }
            parking = tempParking;
            isLoaded = true;
            play.setDisable(false);
            updateParking();
            countPlaces.setText(String.valueOf(getCountPlaces()));
        }
    }

    public static Stage getStage() {
        return stage;
    }

    public static Parking getParking() {
        return parking;
    }

    public static void setParking(Parking parking) {
        ModellingController.parking = parking;
    }

    public static int getCashBox() {
        return cashBox;
    }

    public static void setCashBox(int cashBox) {
        ModellingController.cashBox = cashBox;
    }

    @FXML
    private void playClick(){
        if(isPaused){
            isPaused = false;
            timeThread.resumeThread();
            flowThread.resumeThread();
            Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
            for (Thread thread : threads.keySet()) {
                if(thread.getName().contains("CarThread")){
                    CarThread carThread = (CarThread)thread;
                    carThread.resumeThread();
                }
            }
        }
        else {
            if(isLoaded && isParameterized) {
                cashBox = 0;
                averagePlaces.setText("");
                profit.setText("");
                play.setDisable(true);
                pause.setDisable(false);
                stop.setDisable(false);
                slow.setDisable(false);
                fast.setDisable(false);
                profit.setText("-");
                averagePlaces.setText("-");
                flowThread = new FlowThread(anchorPaneFlow, gridPane, countNoFreePlaces, clock);
                flowThread.setName("TransportFlowThread");
                flowThread.setDaemon(true);
                calendar.set(Calendar.HOUR_OF_DAY, hoursStart);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                timeThread = new TimeThread(clock);
                timeThread.setName("TimeThread");
                timeThread.setDaemon(true);
                flowThread.start();
                timeThread.start();
            }
        }
    }

    @FXML
    private void stopClick(){
        isStopped = true;
        isLoaded = true;
        fast.setDisable(true);
        slow.setDisable(true);
        pause.setDisable(true);
        play.setDisable(false);
        profit.setText(String.valueOf(cashBox));
        menuSettings.setDisable(true);
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.set(Calendar.HOUR_OF_DAY, hoursStart);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        int differenceMin = (int)(calendar.getTimeInMillis() - startCalendar.getTimeInMillis())/60000;
        if(differenceMin == 0){
            differenceMin++;
        }
        averagePlaces.setText(String.valueOf((int)countCars/differenceMin));
        countNoFreePlaces.setText("0");
        for(int i = 0; i < parking.getHorizontalSize(); i++){
            for(int j = 0; j < parking.getVerticalSize(); j++){
                parking.getParkingCells()[i][j].setOccupied(false);
            }
        }
        Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
        for (Thread thread : threads.keySet()) {
            if(thread.getName().equals("TimeThread") || thread.getName().equals("TransportFlowThread")) {
                thread.interrupt();
            }
            else if(thread.getName().contains("CarThread")){
                CarThread carThread = (CarThread)thread;
                try {
                    carThread.finish();
                }
                catch (Throwable e){
                    e.printStackTrace();
                }
            }
        }
        updateParking();
    }

    @FXML
    private void pauseClick(){
        isPaused = true;
        play.setDisable(false);
        Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
        for (Thread thread : threads.keySet()) {
            if(thread.getName().equals("TimeThread")){
                TimeThread timeThread = (TimeThread)thread;
                timeThread.pauseThread();
            }
            else if(thread.getName().equals("TransportFlowThread")){
                FlowThread flowThread = (FlowThread)thread;
                flowThread.pauseThread();
            }
            else if(thread.getName().contains("CarThread")){
                CarThread carThread = (CarThread)thread;
                carThread.interrupt();
            }
        }
    }

    @FXML
    private void fastClick(){
        if(booster.getCode() != 6) {
            booster = Booster.getBooster(booster.getCode() + 1);
            System.out.println(clock.getText() + " Ускорение в " + booster.getBoost() + " раз");
            if(booster != null) {
                boostThreads(booster.getBoost());
            }
        }
    }

    @FXML
    private void slowClick(){
        if(booster.getCode() != -6) {
            booster = Booster.getBooster(booster.getCode() - 1);
            System.out.println(clock.getText() + " Ускорение в " + booster.getBoost() + " раз");
            if(booster != null) {
                boostThreads(booster.getBoost());
            }
        }
    }

    private void boostThreads(double booster){
        //timeThread.pauseThread();
        //flowThread.pauseThread();
        timeThread.setBooster(booster);
        flowThread.setBooster(booster);
        Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
        for (Thread thread : threads.keySet()) {
            if(thread.getName().contains("CarThread")){
                CarThread carThread = (CarThread)thread;
                carThread.interrupt();
                carThread.setBooster(booster);
                carThread.resumeThread();
            }
        }
        //timeThread.resumeThread();
        //flowThread.resumeThread();
    }

    private ColorInput getWhiteColorEffect(double height, double width){
        ColorInput colorInput = new ColorInput();
        colorInput.setHeight(height);
        colorInput.setWidth(width);
        colorInput.setPaint(Color.WHITE);
        return colorInput;
    }

    private ColorAdjust getGrayColorEffect(){
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0.6);
        colorAdjust.setContrast(0.09);
        colorAdjust.setHue(-0.53);
        colorAdjust.setSaturation(-0.98);
        return colorAdjust;
    }

    private int getCountPlaces(){
        int counter = 0;
        for(int i = 0; i < parking.getHorizontalSize(); i++){
            for(int j = 0; j < parking.getVerticalSize(); j++){
                if(parking.getParkingCells()[i][j].getPattern().getPatternType() == PatternType.CAR || parking.getParkingCells()[i][j].getPattern().getPatternType() == PatternType.TRUCK_HEAD){
                    counter++;
                }
            }
        }
        return counter;
    }

    @FXML
    private void infoSystem() throws URISyntaxException, IOException {
        URI uri = new URI("http://localhost:63342/Parking_App/ParkingApp/sample/web/Site.html");
        java.awt.Desktop.getDesktop().browse(uri);
    }

    @FXML
    private void infoCreators() throws IOException{
        Parent helpWindow = FXMLLoader.load(getClass().getResource("../fxmls/infocreators.fxml"));
        Stage helpStage = new Stage();
        helpStage.setResizable(false);
        helpStage.setTitle("О разработчиках");
        helpStage.setScene(new Scene(helpWindow, 600, 345));
        helpStage.showAndWait();
    }

    private void drawParkingInit(){
        for(int i = 0; i < parking.getHorizontalSize(); i++){
            for(int j = 0; j < parking.getVerticalSize(); j++){
                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(parking.getParkingCells()[i][j].getPattern().getPatternType().getPath())));
                //imageView.setFitHeight((double)316/15);
                //imageView.setFitWidth((double)303/15);
                //imageView.setPreserveRatio(true);
                imageView.setEffect(getWhiteColorEffect(imageView.getImage().getHeight(), imageView.getImage().getWidth()));
                GridPane.setHalignment(imageView, HPos.CENTER);
                GridPane.setValignment(imageView, VPos.CENTER);
                GridPane.setMargin(imageView, new Insets(10));
                gridPane.add(imageView, j, i);
            }
        }
    }

    private void drawParking(){
        removeAllChildren();
        parking = new Parking(parking.getHorizontalSize(), parking.getVerticalSize());
        double height = 316;
        double width = 303;
        int max = Math.max(parking.getHorizontalSize(), parking.getVerticalSize());
        for(int i = 0; i < parking.getHorizontalSize(); i++){
            for(int j = 0; j < parking.getVerticalSize(); j++){
                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(parking.getParkingCells()[i][j].getPattern().getPatternType().getPath())));
                imageView.setFitWidth(width/(max + 2));
                imageView.setFitHeight(height/(max + 2));
                imageView.setPreserveRatio(true);
                // imageView.setEffect(getWhiteColorEffect(height/(max + 2), width/(max + 2)));
                GridPane.setHalignment(imageView, HPos.CENTER);
                GridPane.setValignment(imageView, VPos.CENTER);
                GridPane.setMargin(imageView, new Insets(10));
                gridPane.add(imageView, j, i);
            }
        }
    }

    private void removeAllChildren(){
        ObservableList<Node> observableList = gridPane.getChildren();
        int size = observableList.size();
        if (size > 1) {
            observableList.subList(1, size).clear();
        }
    }

    @FXML
    private void loadFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src/parkings"));
        File file = fileChooser.showOpenDialog(new Stage());
        if(file != null) {
            int numPoint = file.getName().lastIndexOf('.');
            if (numPoint > 0 && file.getName().substring(numPoint + 1).equals("park")) {
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    Parking readParking = null;
                    try {
                        readParking = (Parking) objectInputStream.readObject();
                    } catch (Exception ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка");
                        alert.setHeaderText(null);
                        alert.setContentText("Файл с топологией парковки поврежден! Открытие невозможно. ");
                        alert.showAndWait();
                        return;
                    }
                    int horizontalLength = readParking.getHorizontalSize();
                    int verticalLength = readParking.getVerticalSize();
                    int difHor = Math.abs(horizontalLength - parking.getHorizontalSize());
                    int difVer = Math.abs(verticalLength - parking.getVerticalSize());
                    if (horizontalLength < parking.getHorizontalSize()) {
                        for (int i = 0; i < difHor; i++) {
                            parking.setHorizontalSize(parking.getHorizontalSize() - 1);
                            changeHorizontalSize();
                        }
                    }
                    if (horizontalLength > parking.getHorizontalSize()) {
                        for (int i = 0; i < difHor; i++) {
                            parking.setHorizontalSize(parking.getHorizontalSize() + 1);
                            changeHorizontalSize();
                        }
                    }
                    if (verticalLength < parking.getVerticalSize()) {
                        for (int i = 0; i < difVer; i++) {
                            parking.setVerticalSize(parking.getVerticalSize() - 1);
                            changeVerticalSize();
                        }
                    }
                    if (verticalLength > parking.getVerticalSize()) {
                        for (int i = 0; i < difVer; i++) {
                            parking.setVerticalSize(parking.getVerticalSize() + 1);
                            changeVerticalSize();
                        }
                    }
                    parking = readParking;
                    updateParking();
                    isLoaded = true;
                    if(isParameterized) {
                        play.setDisable(false);
                    }
                    countPlaces.setText(String.valueOf(getCountPlaces()));
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void toSettings() throws IOException{
        Parent settingsWindow = FXMLLoader.load(getClass().getResource("../fxmls/settings.fxml"));
        Stage settingsStage = new Stage();
        SettingsController.setStage(settingsStage);
        settingsStage.setResizable(false);
        settingsStage.setTitle("Ввод и настройка параметров");
        settingsStage.setScene(new Scene(settingsWindow, 470, 500));
        settingsStage.showAndWait();
        if(isParameterized&&isLoaded){
            play.setDisable(false);
        }
    }

    private void changeHorizontalSize(){
        int lengthHorizontal = gridPane.getRowConstraints().size();
        RowConstraints rowConstraints = gridPane.getRowConstraints().get(lengthHorizontal-1);
        if(parking.getHorizontalSize() > lengthHorizontal){
            gridPane.getRowConstraints().add(rowConstraints); //view
        }
        else if(parking.getHorizontalSize() < lengthHorizontal){
            removeAllChildren();
            gridPane.getRowConstraints().remove(lengthHorizontal-1); //view
        }
        drawParking();
    }

    private void changeVerticalSize(){
        int lengthVertical = gridPane.getColumnConstraints().size();
        ColumnConstraints columnConstraints = gridPane.getColumnConstraints().get(lengthVertical-1);
        if(parking.getVerticalSize() > lengthVertical){
            gridPane.getColumnConstraints().add(columnConstraints); //view
        }
        else if(parking.getVerticalSize() < lengthVertical){
            removeAllChildren();
            gridPane.getColumnConstraints().remove(lengthVertical-1); //view
        }
        drawParking();
    }

    private void updateParking(){
        removeAllChildren();
        double height = 316;
        double width = 303;
        int max = Math.max(parking.getHorizontalSize(), parking.getVerticalSize());
        for(int i = 0; i < parking.getHorizontalSize(); i++){
            for(int j = 0; j < parking.getVerticalSize(); j++){
                ParkingCell parkingCell = parking.getParkingCells()[i][j];
                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(parkingCell.getPattern().getPatternType().getPath())));
                imageView.setFitWidth(width / (max + 1));
                imageView.setFitHeight(height / (max + 1));
                imageView.setPreserveRatio(true);
                imageView.setEffect(getGrayColorEffect());
                if(parkingCell.getPattern().getPatternType() != PatternType.TRUCK_HEAD && parkingCell.getPattern().getPatternType() != PatternType.TRUCK_TAIL && parkingCell.getPattern().getPatternType() != PatternType.IN && parkingCell.getPattern().getPatternType() != PatternType.OUT) {
                    if (parkingCell.getPattern().getPatternType() == PatternType.EMPTY) {
                        imageView.setEffect(getWhiteColorEffect(height / (max + 1), width / (max + 1)));
                    }
                }
                else if(parkingCell.getPattern().getPatternType() == PatternType.TRUCK_HEAD || parkingCell.getPattern().getPatternType() == PatternType.TRUCK_TAIL){
                    switch (parkingCell.getPattern().getRotation()){
                        case NORTH:{
                            imageView.setRotate(-90);
                            break;
                        }
                        case EAST:{
                            imageView.setRotate(0);
                            break;
                        }
                        case SOUTH:{
                            imageView.setRotate(90);
                            break;
                        }
                        case WEST:{
                            imageView.setScaleX(-1);
                            break;
                        }
                    }
                }
                else if(parkingCell.getPattern().getPatternType() == PatternType.IN){
                    switch (parkingCell.getPattern().getRotation()){
                        case NORTH:{
                            imageView.setRotate(180);
                            break;
                        }
                        case EAST:{
                            imageView.setRotate(-90);
                            break;
                        }
                        case SOUTH:{
                            imageView.setRotate(0);
                            break;
                        }
                        case WEST:{
                            imageView.setRotate(90);
                            break;
                        }
                    }
                }
                else if(parkingCell.getPattern().getPatternType() == PatternType.OUT){
                    switch (parkingCell.getPattern().getRotation()){
                        case NORTH:{
                            imageView.setRotate(0);
                            break;
                        }
                        case EAST:{
                            imageView.setRotate(90);
                            break;
                        }
                        case SOUTH:{
                            imageView.setRotate(180);
                            break;
                        }
                        case WEST:{
                            imageView.setRotate(-90);
                            break;
                        }
                    }
                }
                GridPane.setHalignment(imageView, HPos.CENTER);
                GridPane.setValignment(imageView, VPos.CENTER);
                GridPane.setMargin(imageView, new Insets(10));
                gridPane.add(imageView, j, i);
            }
        }
    }
}
    /*
    СДЕЛАТЬ:
    1. Появление машин на шоссе (СДЕЛАНО)
    2. Появление типа автомобиля по вероятности (СДЕЛАНО)
    3. Работа с часами (СДЕЛАНО)
    4. Остановка потоков (СДЕЛАНО)
    5. Пауза потоков (СДЕЛАНО)
    6. Ускорение и замедление машин (СДЕЛАНО)
    7. Заезд на парковку по вероятности
    8. Построение миаршрута до парковочного места
    */
