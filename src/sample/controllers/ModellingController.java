package sample.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Random;

import javafx.scene.control.Label;

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

    public void initialize(){
        drawGridInit();
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
                    int[][] imageViewMatrix = (int[][]) objectInputStream.readObject();
                    int lengthHorizontal = imageViewMatrix.length;
                    int lengthVertical = imageViewMatrix[0].length;
                    removeAllChildren();
                    changeGrid(lengthHorizontal, lengthVertical);
                    drawGrid(lengthHorizontal, lengthVertical);
                    int number = 1;
                    for (int i = 0; i < imageViewMatrix.length; i++) {
                        for (int j = 0; j < imageViewMatrix[i].length; j++) {
                            ImageView imageView = (ImageView) gridPane.getChildren().get(number);
                            switch (imageViewMatrix[i][j]) {
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
                            if(imageViewMatrix[i][j] != 0) {
                                imageView.setEffect(new GaussianBlur(4));
                            }
                            number++;
                        }
                    }
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
        FlowThread flowThread = new FlowThread(anchorPaneFlow);
        flowThread.setDaemon(true);
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        TimeThread timeThread = new TimeThread(calendar, clock);
        flowThread.start();
        timeThread.start();
    }

    @FXML
    private void stopModelling(){

    }
}

class CarThread extends Thread{
    private AnchorPane anchorPane;
    private int number;

    public CarThread(AnchorPane anchorPane, int number){
        this.anchorPane = anchorPane;
        this.number = number;
    }
    @Override
    public void run() {
        for(int i = 0; i < anchorPane.getWidth() + 50; i = i + 10){
            Platform.runLater(new UpdaterCar(i, anchorPane, number));
            try {
                sleep(100);
            }
            catch (InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }
}

class UpdaterCar implements Runnable{
    private int number;
    private AnchorPane anchorPane;
    private int i;

    public UpdaterCar(int i, AnchorPane anchorPane, int number){
        this.number = number;
        this.i = i;
        this.anchorPane = anchorPane;
    }
    @Override
    public void run() {
        anchorPane.getChildren().get(number).setLayoutX(i);
    }
}

class UpdaterAnchorPane implements Runnable{
    private int number;
    private AnchorPane anchorPane;

    public UpdaterAnchorPane(int number, AnchorPane anchorPane) {
        this.number = number;
        this.anchorPane = anchorPane;
    }

    @Override
    public void run() {
        double probability = 0.5;
        ImageView imageView = null;
        if(new Random().nextDouble() <= probability) {
            imageView = new ImageView(new Image(getClass().getResourceAsStream("../images/carModelling.JPG")));
            imageView.setFitWidth(45);
        }
        else {
            imageView = new ImageView(new Image(getClass().getResourceAsStream("../images/truckModelling.JPG")));
            imageView.setFitWidth(55);
        }
        imageView.setFitHeight(35);
        anchorPane.getChildren().add(number, (Node)imageView);
        anchorPane.getChildren().get(number).setLayoutX(0);
        anchorPane.getChildren().get(number).setLayoutY(0);
    }
}


class FlowThread extends Thread{
    private AnchorPane anchorPane;

    public FlowThread(AnchorPane anchorPane){
        this.anchorPane = anchorPane;
    }

    @Override
    public void run() {
        int number = 1;
        while (number < 100){
            Platform.runLater(new UpdaterAnchorPane(number, anchorPane));
            CarThread carThread = new CarThread(anchorPane, number);
            carThread.start();
            try {
                int time = getTimeUniform();
                sleep(time * 1000);
            }
            catch (InterruptedException ex){
                ex.printStackTrace();
            }
            number++;
        }
    }

    private int getTimeUniform(){
        int a = 1;
        int b = 5;
        Random random = new Random();
        return a + random.nextInt(b-a+1);
    }
}

class TimeThread extends Thread{
    private Calendar calendar;
    private Label clock;

    public TimeThread(Calendar calendar, Label clock){
        this.calendar = calendar;
        this.clock = clock;
    }
    @Override
    public void run() {
        while (!interrupted()) {
            Platform.runLater(new UpdaterTime(calendar, clock));
            try {
                sleep(1000);
            }
            catch (InterruptedException ignored){

            }
            calendar.add(Calendar.SECOND, 1);
        }
    }
}

class UpdaterTime implements Runnable{
    private Calendar calendar;
    private Label clock;

    public UpdaterTime(Calendar calendar, Label clock){
        this.calendar = calendar;
        this.clock = clock;
    }
    @Override
    public void run() {
        if (calendar.get(Calendar.MINUTE) < 10 && calendar.get(Calendar.SECOND) < 10) {
            clock.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":0" + calendar.get(Calendar.MINUTE) + ":0" + calendar.get(Calendar.SECOND));
        } else if (calendar.get(Calendar.MINUTE) < 10 && calendar.get(Calendar.SECOND) >= 10) {
            clock.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":0" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
        } else if (calendar.get(Calendar.MINUTE) > 10 && calendar.get(Calendar.SECOND) < 10) {
            clock.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":0" + calendar.get(Calendar.SECOND));
        } else {
            clock.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
        }
    }
}

    /*
    СДЕЛАТЬ:
    1. Появление машин на шоссе (СДЕЛАНО)
    2. Появление типа автомобиля по вероятности (СДЕЛАНО)
    3. Работа с часами (СДЕЛАНО)
    4. Остановка потоков
    5. Пауза потоков
    6. Заезд на парковку по вероятности
    7. Построение миаршрута до парковочного места
    */
