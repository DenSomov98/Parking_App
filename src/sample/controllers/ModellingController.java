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
import sample.threads.*;
import sample.enums.*;

public class ModellingController {
    @FXML
    private static Stage stage;
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

    private int[][] maskParking;


    public void initialize(){
        play.setDisable(true);
        pause.setDisable(true);
        stop.setDisable(true);
        slow.setDisable(true);
        fast.setDisable(true);
        calendar = new GregorianCalendar();
        booster = Booster.BOOSTER_DEFAULT;
        drawGridInit();
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

    private void drawGridInit(){
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                ImageView imageView = new ImageView();
                imageView.setImage(new Image(getClass().getResourceAsStream("../images/empty.JPG")));
                imageView.setFitHeight((double)316/7);
                imageView.setFitWidth((double)303/7);
                ColorInput colorInput = new ColorInput();
                colorInput.setHeight(imageView.getFitHeight());
                colorInput.setWidth(imageView.getFitWidth());
                colorInput.setPaint(Color.WHITE);
                imageView.setEffect(colorInput);
                GridPane.setHalignment(imageView, HPos.CENTER);
                GridPane.setValignment(imageView, VPos.CENTER);
                gridPane.add(imageView, j, i);
            }
        }
    }

    @FXML
    private void loadFile(){
        FileChooser fileChooser = new FileChooser();
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
        }
    }

    private void removeAllChildren(){
        ObservableList<Node> observableList = gridPane.getChildren();
        int size = observableList.size();
        for(int i = 1; i < size; i++){
            observableList.remove(1);
        }
    }

    private void drawGrid(int lengthH, int lengthV){
        double height = gridPane.getPrefHeight();
        double width = gridPane.getPrefWidth();
        int max = Math.max(lengthH, lengthV);
        for(int i = 0; i < lengthH; i++){
            for(int j = 0; j < lengthV; j++){
                ImageView imageView = new ImageView();
                imageView.setImage(new Image(getClass().getResourceAsStream("../images/empty.JPG")));
                imageView.setFitWidth(width/(max + 2));
                imageView.setFitHeight(height/(max + 2));
                imageView.setPreserveRatio(true);
                ColorInput colorInput = new ColorInput();
                colorInput.setHeight(width/(max + 2));
                colorInput.setWidth(height/(max + 2));
                colorInput.setPaint(Color.WHITE);
                imageView.setEffect(colorInput);
                GridPane.setHalignment(imageView, HPos.CENTER);
                GridPane.setValignment(imageView, VPos.CENTER);
                GridPane.setMargin(imageView, new Insets(10));
                gridPane.add(imageView, j, i);
            }
        }
    }

    private void changeGrid(int lengthH, int lengthV){
        int countRows = gridPane.getRowConstraints().size();
        int countColumns = gridPane.getColumnConstraints().size();
        RowConstraints rowConstraints = gridPane.getRowConstraints().get(countRows-1);
        ColumnConstraints columnConstraints = gridPane.getColumnConstraints().get(countColumns-1);
        if(countRows < lengthH){
            for(int i = 0; i < lengthH-countRows; i++){
                gridPane.getRowConstraints().add(rowConstraints);
            }
        }
        else if(countRows > lengthH){
            for(int i = 0; i < countRows-lengthH; i++){
                gridPane.getRowConstraints().remove(countRows-1);
                countRows--;
            }
        }
        else if(countColumns < lengthV){
            for(int i = 0; i < lengthV-countColumns; i++){
                gridPane.getColumnConstraints().add(columnConstraints);
            }
        }
        else if(countColumns > lengthV){
            for(int i = 0; i < countColumns-lengthH; i++){
                gridPane.getColumnConstraints().remove(countColumns-1);
                countColumns--;
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

    private int getCoordinateXOfIn(){
        for(int i = 0; i < maskParking.length; i++) {
            for (int j = 0; j < maskParking[0].length; j++) {
                if (maskParking[i][j] == 2) {
                    int index = (i * maskParking[0].length) + j + 1;
                    return (int) gridPane.getChildren().get(index).getBoundsInParent().getMaxX();
                }
            }
        }
        return -1;
    }

    private PairIJ getPairIJIn(){
        for(int i = 0; i < maskParking.length; i++){
            for(int j = 0; j < maskParking[0].length; j++){
                if(maskParking[i][j] == 2){
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
