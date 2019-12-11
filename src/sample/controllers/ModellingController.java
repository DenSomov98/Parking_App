package sample.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.effect.Blend;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Random;

import javafx.scene.control.Label;
import jdk.internal.util.xml.impl.Pair;
import sample.PairIJ;
import sample.models.Parking;
import sample.models.ParkingCell;
import sample.threads.*;
import sample.enums.*;

public class ModellingController {
    private static Stage stage;
    private Parking parking;
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
    private Label profitDay;
    @FXML
    private Label averagePlacesDay;
    @FXML
    private GridPane gridPane;
    @FXML
    private AnchorPane anchorPaneFlow;

    private Calendar calendar;
    private Booster booster;

    private FlowThread flowThread;
    private TimeThread timeThread;

    private boolean isStopped;
    private boolean isPaused;
    private boolean isLoaded;
    private static boolean isParameterized;

    private LawType flowType;
    private int flowParamFirst;
    private int flowParamSecond;

    private LawType stayType;
    private int stayParamFirst;
    private int stayParamSecond;

    public void initialize(){
        parking = new Parking(5, 5);
        play.setDisable(true);
        pause.setDisable(true);
        stop.setDisable(true);
        slow.setDisable(true);
        fast.setDisable(true);
        calendar = new GregorianCalendar();
        booster = Booster.BOOSTER_DEFAULT;
        drawParkingInit();
        //addAnchorFlowClickEvent();
        flowType = LawType.UNIFORM;
        flowParamFirst = 1;
        flowParamSecond = 5;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        ModellingController.stage = stage;
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
        double height = gridPane.getHeight();
        double width = gridPane.getWidth();
        int max = Math.max(parking.getHorizontalSize(), parking.getVerticalSize());
        for(int i = 0; i < parking.getHorizontalSize(); i++){
            for(int j = 0; j < parking.getVerticalSize(); j++){
                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(parking.getParkingCells()[i][j].getPattern().getPatternType().getPath())));
                imageView.setFitWidth(width/(max + 2));
                imageView.setFitHeight(height/(max + 2));
                imageView.setPreserveRatio(true);
                imageView.setEffect(getWhiteColorEffect(height/(max + 2), width/(max + 2)));
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
        /*FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("D://Денис/Desktop/parkings"));
        File file = fileChooser.showOpenDialog(new Stage());
        if(file != null){
            int numPoint = file.getName().lastIndexOf('.');
            if( numPoint > 0 && file.getName().substring(numPoint+1).equals("park")) {
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    maskParking = (int[][]) objectInputStream.readObject();
                    int lengthHorizontal = maskParking.length;
                    int lengthVertical = maskParking[0].length;
                    removeAllChildren();
                    changeGrid(lengthHorizontal, lengthVertical);
                    drawGrid(lengthHorizontal, lengthVertical);
                    int number = 1;
                    for (int[] viewMatrix : maskParking) {
                        for (int matrix : viewMatrix) {
                            ImageView imageView = (ImageView) gridPane.getChildren().get(number);
                            switch (matrix) {
                                case 1: {
                                    imageView.setImage(new Image(getClass().getResourceAsStream("../images/car.JPG")));
                                    imageView.setEffect(new Blend());
                                    break;
                                }
                                case 2: {
                                    imageView.setImage(new Image(getClass().getResourceAsStream("../images/arrowin.JPG")));
                                    imageView.setEffect(new Blend());
                                    break;
                                }
                                case 3: {
                                    imageView.setImage(new Image(getClass().getResourceAsStream("../images/arrowout.JPG")));
                                    imageView.setEffect(new Blend());
                                    break;
                                }
                                case 4: {
                                    imageView.setImage(new Image(getClass().getResourceAsStream("../images/cash.JPG")));
                                    imageView.setEffect(new Blend());
                                    break;
                                }
                                case 5: {
                                    imageView.setImage(new Image(getClass().getResourceAsStream("../images/road.JPG")));
                                    imageView.setEffect(new Blend());
                                    break;
                                }
                                case 6: {
                                    imageView.setImage(new Image(getClass().getResourceAsStream("../images/truck.JPG")));
                                    imageView.setEffect(new Blend());
                                    break;
                                }
                            }
                            if (matrix != 0) {
                                imageView.setEffect(new GaussianBlur(4));
                            }
                            number++;
                        }
                    }
                    isLoaded = true;
                    play.setDisable(false);
                }
                catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText("Ошибка при загрузке файла!");
                alert.showAndWait();
            }
        }*/
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
                    } catch (ClassNotFoundException | ClassCastException ex) {
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
                    play.setDisable(false);
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
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
        double height = gridPane.getHeight();
        double width = gridPane.getWidth();
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
            if(isStopped){
                isStopped = false;
                timeThread.setCanWork(true);
                flowThread.setCanWork(true);
                Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
                for (Thread thread : threads.keySet()) {
                    if(thread.getName().contains("CarThread")){
                        CarThread carThread = (CarThread)thread;
                        carThread.setCanWork(true);
                    }
                }
            }
            if(isLoaded) {
                play.setDisable(true);
                pause.setDisable(false);
                stop.setDisable(false);
                slow.setDisable(false);
                fast.setDisable(false);
                flowThread = new FlowThread(anchorPaneFlow, booster.getBoost(), flowParamFirst, flowParamSecond, flowType, getCoordinateXOfIn(), gridPane, getPairIJIn());
                flowThread.setName("TransportFlowThread");
                flowThread.setDaemon(true);
                calendar.set(Calendar.HOUR_OF_DAY, 12);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                timeThread = new TimeThread(calendar, clock, booster.getBoost());
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
        Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
        for (Thread thread : threads.keySet()) {
            if(thread.getName().equals("TimeThread") || thread.getName().equals("TransportFlowThread") || thread.getName().contains("CarThread")) {
                thread.interrupt();
            }
        }
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
                carThread.pauseThread();
            }
        }
    }

    @FXML
    private void fastClick(){
        if(booster.getCode() != 6) {
            booster = Booster.getBooster(booster.getCode() + 1);
            if(booster != null) {
                boostThreads(booster.getBoost());
            }
        }
    }

    @FXML
    private void slowClick(){
        if(booster.getCode() != -6) {
            booster = Booster.getBooster(booster.getCode() - 1);
            if(booster != null) {
                boostThreads(booster.getBoost());
            }
        }
    }

    private void boostThreads(double booster){
        timeThread.pauseThread();
        flowThread.pauseThread();
        timeThread.setBooster(booster);
        flowThread.setBooster(booster);
        timeThread.resumeThread();
        flowThread.resumeThread();
        Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
        for (Thread thread : threads.keySet()) {
            if(thread.getName().contains("CarThread")){
                CarThread carThread = (CarThread)thread;
                carThread.pauseThread();
                carThread.setBooster(booster);
                carThread.resumeThread();
            }
        }
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

    private int getCoordinateXOfIn(){
        for(int i = 0; i < parking.getHorizontalSize(); i++){
            for(int j = 0; j < parking.getVerticalSize(); j++){
                if(parking.getParkingCells()[i][j].getPattern().getPatternType() == PatternType.IN){
                    int index = coordinatesToIndex(i, j);
                    return (int)gridPane.getChildren().get(index).getBoundsInParent().getMaxX();
                }
            }
        }
        return -1;
        /*for(int i = 0; i < maskParking.length; i++) {
            for (int j = 0; j < maskParking[0].length; j++) {
                if (maskParking[i][j] == 2) {
                    int index = (i * maskParking[0].length) + j + 1;
                    return (int) gridPane.getChildren().get(index).getBoundsInParent().getMaxX();
                }
            }
        }
        return -1;*/
    }

    private int coordinatesToIndex(int i, int j){
        return (i * parking.getVerticalSize() + j + 1);
    }

    private PairIJ getPairIJIn(){
        for(int i = 0; i < parking.getHorizontalSize(); i++){
            for(int j = 0; j < parking.getVerticalSize(); j++){
                if(parking.getParkingCells()[i][j].getPattern().getPatternType() == PatternType.IN){
                    return new PairIJ(i, j);
                }
            }
        }
        return null;
    }


    private void addAnchorFlowClickEvent(){
        gridPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                countNoFreePlaces.setText(String.valueOf(event.getX()));
                countPlaces.setText(String.valueOf(event.getY()));
            }
        });
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
