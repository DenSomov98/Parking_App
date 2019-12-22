package sample.controllers;

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;
import com.sun.jndi.toolkit.url.Uri;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jdk.internal.util.xml.impl.Pair;
import sample.utils.PairIJ;
import sample.enums.PatternType;
import sample.enums.Rotation;
import sample.models.Parking;
import sample.models.ParkingCell;
import sample.models.Pattern;
import sample.utils.WaveAlg;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class ConstructionController {
    private static Stage stage;
    private Parking parking;

    @FXML
    protected ImageView car;
    @FXML
    private ImageView arrowIn;
    @FXML
    private ImageView arrowOut;
    @FXML
    private ImageView cash;
    @FXML
    private ImageView road;
    @FXML
    private ImageView truck;
    @FXML
    private ImageView cross;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Spinner<Integer> spinnerHorizontal; // спиннер размеров по горизонтали
    @FXML
    private Spinner<Integer> spinnerVertical; // спиннер размеров по вертикали
    @FXML
    private GridPane gridPane; // сетка
    @FXML
    private Button check;
    @FXML
    private Button toModelling;

    private ImageView emptyPattern;
    private ImageView[] imageViews;
    private int countTrucks;
    private boolean isGreen; // выбран активный шаблон
    private boolean isRed; // выбран крест
    private boolean isBlue; // выбран шаблон для перемещения
    private boolean isPurple; // добавляется грузовик
    private boolean isMovedTruck; //перемещается грузовик
    private boolean isCorrectTopology; // признак корректности топологии
    private ImageView currentImageView; // текущее изображение
    private Pattern currentPattern; // текущий шаблон
    private Pattern chosenPattern; //выбранный шаблон
    private int indexRowChosen; // выбранный номер строчки
    private int indexColumnChosen; // выбранный номер столбца
    private int indexRowTruckHead;
    private int indexColumnTruckHead;

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        ConstructionController.stage = stage;
    }

    //инициализация формы конструирования
    @FXML
    private void initialize() {
        parking = new Parking(5, 5);
        imageViews = new ImageView[]{car, arrowIn, arrowOut, cash, road, truck, cross};
        emptyPattern = new ImageView(new Image(getClass().getResourceAsStream(PatternType.EMPTY.getPath())));
        spinnerHorizontal.setEditable(false);
        spinnerVertical.setEditable(false);
        drawParkingInit();
        addGridEvent(); // добавление методов обработки нажатий мыши на каждую ячейку
    }

    // изначальное рисование парковки
    private void drawParkingInit(){
        for(int i = 0; i < parking.getHorizontalSize(); i++){
            for(int j = 0; j < parking.getVerticalSize(); j++){
                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(parking.getParkingCells()[i][j].getPattern().getPatternType().getPath())));
                imageView.setFitHeight((double)373/7);
                imageView.setFitWidth((double)347/7);
                imageView.setPreserveRatio(true);
                imageView.setEffect(getWhiteColorEffect(imageView.getImage().getHeight(), imageView.getImage().getWidth()));
                GridPane.setHalignment(imageView, HPos.CENTER);
                GridPane.setValignment(imageView, VPos.CENTER);
                GridPane.setMargin(imageView, new Insets(10));
                gridPane.add(imageView, j, i);
            }
        }
    }

    // рисование парковки (пустой) //view
    private void drawParking(){
        removeAllChildren();
        parking = new Parking((spinnerHorizontal.getValue()), spinnerVertical.getValue());
        double height = gridPane.getHeight();
        double width = gridPane.getWidth();
        int max = Math.max(spinnerVertical.getValue(), spinnerHorizontal.getValue());
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

    // обновление парковки (ОБЪЕДИНИТЬ ВЕТКИ) //view
    private void updateParking(){
        removeAllChildren();
        double height = gridPane.getHeight();
        double width = gridPane.getWidth();
        int max = Math.max(spinnerVertical.getValue(), spinnerHorizontal.getValue());
        for(int i = 0; i < parking.getHorizontalSize(); i++){
            for(int j = 0; j < parking.getVerticalSize(); j++){
                ParkingCell parkingCell = parking.getParkingCells()[i][j];
                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(parkingCell.getPattern().getPatternType().getPath())));
                imageView.setFitWidth(width / (max + 1));
                imageView.setFitHeight(height / (max + 1));
                imageView.setPreserveRatio(true);
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
        addGridEvent();
    }

    @FXML // метод изменения размера по горизонтали
    private void changeHorizontalSize(){
        int lengthHorizontal = gridPane.getRowConstraints().size();
        RowConstraints rowConstraints = gridPane.getRowConstraints().get(lengthHorizontal-1);
        if(spinnerHorizontal.getValue() > lengthHorizontal){
            gridPane.getRowConstraints().add(rowConstraints); //view
        }
        else if(spinnerHorizontal.getValue() < lengthHorizontal){
            removeAllChildren();
            gridPane.getRowConstraints().remove(lengthHorizontal-1); //view
        }
        drawParking();
        addGridEvent();
    }

    @FXML // метод изменения размеров по вертикали
    private void changeVerticalSize(){
        int lengthVertical = gridPane.getColumnConstraints().size();
        ColumnConstraints columnConstraints = gridPane.getColumnConstraints().get(lengthVertical-1);
        if(spinnerVertical.getValue() > lengthVertical){
            gridPane.getColumnConstraints().add(columnConstraints); //view
        }
        else if(spinnerVertical.getValue() < lengthVertical){
            removeAllChildren();
            gridPane.getColumnConstraints().remove(lengthVertical-1); //view
        }
        drawParking();
        addGridEvent();
    }

    //обработчик нажатия мыши на каждую из клеток парковки
    private void addGridEvent(){
        gridPane.getChildren().forEach(item-> item.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ImageView source = (ImageView) event.getSource();
                int indexRow = GridPane.getRowIndex(source);
                int indexColumn = GridPane.getColumnIndex(source);
                Pattern sourcePattern = parking.getParkingCells()[indexRow][indexColumn].getPattern();
                //СЛУЧАЙ ДОБАВЛЕНИЯ ШАБЛОНА ИЗ ОДНОЙ КЛЕТКИ (+)
                if(event.getButton() == MouseButton.PRIMARY && isGreen && !isBlue && currentPattern.getPatternType() != PatternType.EMPTY && currentPattern.getPatternType() != PatternType.TRUCK_HEAD && sourcePattern.getPatternType() != PatternType.TRUCK_HEAD && sourcePattern.getPatternType() != PatternType.TRUCK_TAIL) {
                    if(currentPattern.getPatternType() == PatternType.IN || currentPattern.getPatternType() == PatternType.OUT){
                        rotateINOUT(currentPattern ,indexRow, indexColumn);
                    }
                    parking.addPattern(indexRow, indexColumn, currentPattern);
                    updateParking();
                    checkChangeSpinnerSize();
                    checkUniqueIcons();
                }
                //СЛУЧАЙ ДОБАВЛЕНИЯ ПЕРЕДНЕЙ ЧАСТИ ГРУЗОВИКА (+)
                else if(event.getButton() == MouseButton.PRIMARY && isGreen && !isBlue && currentPattern.getPatternType() != PatternType.EMPTY && sourcePattern.getPatternType() != PatternType.TRUCK_HEAD && sourcePattern.getPatternType() != PatternType.TRUCK_TAIL && !isPurple){
                    isPurple = true;
                    parking.addPattern(indexRow, indexColumn, new Pattern(PatternType.TRUCK_HEAD));
                    source.setEffect(getBloomEffect(Color.ORANGE));
                    ArrayList<PairIJ> listIndexNearCells = getIndexNearCellsTruck(indexRow, indexColumn);
                    for(PairIJ pair: listIndexNearCells){
                        ImageView imageView = (ImageView)gridPane.getChildren().get(coordinatesToIndex(pair.getI(), pair.getJ()));
                        imageView.setEffect(getShadowEffect(Color.PURPLE));
                    }
                    for(ImageView imageView: imageViews){
                        if(imageView.getImage() != truck.getImage()){
                            imageView.setDisable(true);
                        }
                    }
                    indexRowTruckHead = indexRow;
                    indexColumnTruckHead = indexColumn;
                    checkChangeSpinnerSize();
                }
                //СЛУЧАЙ ДОБАВЛЕНИЯ ЗАДНЕЙ ЧАСТИ ГРУЗОВИКА (+)
                else if(event.getButton() == MouseButton.PRIMARY && isGreen && !isBlue && currentPattern.getPatternType() != PatternType.EMPTY){
                    if(source.getEffect() instanceof InnerShadow) {
                        isPurple = false;
                        Pattern pattern = new Pattern(PatternType.TRUCK_TAIL);
                        countTrucks++;
                        pattern.setId(countTrucks);
                        parking.getParkingCells()[indexRowTruckHead][indexColumnTruckHead].getPattern().setId(countTrucks);
                        if(indexRow == indexRowTruckHead - 1){
                            pattern.setRotation(Rotation.SOUTH);
                            parking.getParkingCells()[indexRowTruckHead][indexColumnTruckHead].getPattern().setRotation(Rotation.SOUTH);
                        }
                        else if(indexRow == indexRowTruckHead + 1){
                            pattern.setRotation(Rotation.NORTH);
                            parking.getParkingCells()[indexRowTruckHead][indexColumnTruckHead].getPattern().setRotation(Rotation.NORTH);
                        }
                        else if(indexColumn == indexColumnTruckHead - 1){
                            pattern.setRotation(Rotation.EAST);
                            parking.getParkingCells()[indexRowTruckHead][indexColumnTruckHead].getPattern().setRotation(Rotation.EAST);
                        }
                        else if(indexColumn == indexColumnTruckHead + 1){
                            pattern.setRotation(Rotation.WEST);
                            parking.getParkingCells()[indexRowTruckHead][indexColumnTruckHead].getPattern().setRotation(Rotation.WEST);
                        }
                        parking.addPattern(indexRow, indexColumn, pattern);
                        updateParking();
                    }
                    for(ImageView imageView: imageViews){
                        if(imageView.getImage() != truck.getImage()){
                            imageView.setDisable(false);
                        }
                    }
                    checkChangeSpinnerSize();
                    checkUniqueIcons();
                }
                //СЛУЧАЙ УДАЛЕНИЯ ШАБЛОНА ИЗ ОДНОЙ КЛЕТКИ (+)
                else if(event.getButton() == MouseButton.PRIMARY&&isRed&&sourcePattern.getPatternType() != PatternType.TRUCK_HEAD && sourcePattern.getPatternType() != PatternType.TRUCK_TAIL){
                    parking.removePattern(indexRow, indexColumn);
                    updateParking();
                    checkChangeSpinnerSize();
                    checkUniqueIcons();
                }
                //СЛУЧАЙ УДАЛЕНИЯ ГРУЗОВИКА (+)
                else if(event.getButton() == MouseButton.PRIMARY&&isRed&&(sourcePattern.getPatternType() == PatternType.TRUCK_HEAD || sourcePattern.getPatternType() == PatternType.TRUCK_TAIL)){
                    int id = sourcePattern.getId();
                    for(int i = 0; i < parking.getHorizontalSize(); i++){
                        for (int j = 0; j < parking.getVerticalSize(); j++){
                            ParkingCell[][] parkingCells = parking.getParkingCells();
                            if(parkingCells[i][j].getPattern().getId() == id){
                                parking.removePattern(i, j);
                            }
                        }
                    }
                    updateParking();
                    checkChangeSpinnerSize();
                    checkUniqueIcons();
                }
                //СЛУЧАЙ ВЫДЕЛЕНИЯ ШАБЛОНА ИЗ ОДНОЙ КЛЕТКИ (+)
                else if(event.getButton() == MouseButton.SECONDARY&&sourcePattern.getPatternType() != PatternType.EMPTY&&sourcePattern.getPatternType() != PatternType.TRUCK_HEAD && sourcePattern.getPatternType() != PatternType.TRUCK_TAIL){
                    checkHighlightIcons();
                    checkHighLightChildren();
                    isBlue = true;
                    isRed = false;
                    isGreen = false;
                    chosenPattern = parking.getParkingCells()[indexRow][indexColumn].getPattern();
                    indexRowChosen = indexRow;
                    indexColumnChosen = indexColumn;
                    source.setEffect(getShadowEffect(Color.BLUE));
                }
                //СЛУЧАЙ ПЕРЕМЕЩЕНИЯ ШАБЛОНА ИЗ ОДНОЙ КЛЕТКИ (+)
                else if(event.getButton() == MouseButton.PRIMARY&&isBlue&&sourcePattern.getPatternType() != PatternType.TRUCK_TAIL && sourcePattern.getPatternType() != PatternType.TRUCK_HEAD&&!isMovedTruck){
                    if(chosenPattern.getPatternType() == PatternType.IN || chosenPattern.getPatternType() == PatternType.OUT){
                        rotateINOUT(chosenPattern ,indexRow, indexColumn);
                    }
                    isBlue = false;
                    parking.removePattern(indexRowChosen, indexColumnChosen);
                    parking.addPattern(indexRow, indexColumn, chosenPattern);
                    chosenPattern = new Pattern(PatternType.EMPTY);
                    updateParking();
                    checkHighLightChildren();
                    checkUniqueIcons();
                }
                //СЛУЧАЙ ВЫДЕЛЕНИЯ ГРУЗОВИКА (+)
                else if(event.getButton() == MouseButton.SECONDARY&&(sourcePattern.getPatternType() == PatternType.TRUCK_HEAD)){
                    checkUniqueIcons();
                    checkHighLightChildren();
                    chosenPattern = sourcePattern;
                    indexRowChosen = indexRow;
                    indexColumnChosen = indexColumn;
                    isBlue = true;
                    isMovedTruck = true;
                    isRed = false;
                    isGreen = false;
                    int id = sourcePattern.getId();
                    for(int i = 0; i < parking.getHorizontalSize(); i++){
                        for(int j = 0; j < parking.getVerticalSize(); j++){
                            ParkingCell parkingCell = parking.getParkingCells()[i][j];
                            if(parkingCell.getPattern().getId() == id){
                                gridPane.getChildren().get(coordinatesToIndex(i, j)).setEffect(getBloomEffect(Color.BLUE));
                            }
                        }
                    }
                }
                //СЛУЧАЙ ПЕРЕМЕЩЕНИЯ ГРУЗОВИКА (+)
                else if(event.getButton() == MouseButton.PRIMARY&&isBlue&&isMovedTruck&&sourcePattern.getPatternType() != PatternType.TRUCK_TAIL && sourcePattern.getPatternType() != PatternType.TRUCK_HEAD){
                    isBlue = false;
                    isMovedTruck = false;
                    if(chosenPattern.getRotation() == Rotation.EAST && indexColumn != 0){
                        parking.addPattern(indexRow, indexColumn, chosenPattern);
                        parking.removePattern(indexRowChosen, indexColumnChosen);
                        parking.addPattern(indexRow, indexColumn-1, new Pattern(PatternType.TRUCK_TAIL, chosenPattern.getRotation(), chosenPattern.getId()));
                        parking.removePattern(indexRowChosen, indexColumnChosen - 1);
                    }
                    else if(chosenPattern.getRotation() == Rotation.WEST && indexColumn != parking.getVerticalSize() - 1){
                        parking.addPattern(indexRow, indexColumn, chosenPattern);
                        parking.removePattern(indexRowChosen, indexColumnChosen);
                        parking.addPattern(indexRow, indexColumn+1, new Pattern(PatternType.TRUCK_TAIL, chosenPattern.getRotation(), chosenPattern.getId()));
                        parking.removePattern(indexRowChosen, indexColumnChosen + 1);
                    }
                    else if(chosenPattern.getRotation() == Rotation.NORTH && indexRow != parking.getHorizontalSize() - 1){
                        parking.addPattern(indexRow, indexColumn, chosenPattern);
                        parking.removePattern(indexRowChosen, indexColumnChosen);
                        parking.addPattern(indexRow + 1, indexColumn, new Pattern(PatternType.TRUCK_TAIL, chosenPattern.getRotation(), chosenPattern.getId()));
                        parking.removePattern(indexRowChosen + 1, indexColumnChosen);
                    }
                    else if(chosenPattern.getRotation() == Rotation.SOUTH && indexRow != 0){
                        parking.addPattern(indexRow, indexColumn, chosenPattern);
                        parking.removePattern(indexRowChosen, indexColumnChosen);
                        parking.addPattern(indexRow - 1, indexColumn, new Pattern(PatternType.TRUCK_TAIL, chosenPattern.getRotation(), chosenPattern.getId()));
                        parking.removePattern(indexRowChosen - 1, indexColumnChosen);
                    }
                    chosenPattern = new Pattern(PatternType.EMPTY);
                    updateParking();
                    checkHighLightChildren();
                    checkUniqueIcons();
                }
                else if(event.getClickCount() == 2 && sourcePattern.getPatternType() == PatternType.IN){
                    Rotation currentRotation = sourcePattern.getRotation();
                    if(indexRow == 0 && indexColumn == 0){
                        if(currentRotation == Rotation.SOUTH){
                            sourcePattern.setRotation(Rotation.EAST);
                        }
                        else{
                            sourcePattern.setRotation(Rotation.SOUTH);
                        }
                    }
                    else if(indexRow == 0 && indexColumn == parking.getVerticalSize() - 1){
                        if(currentRotation == Rotation.SOUTH){
                            sourcePattern.setRotation(Rotation.WEST);
                        }
                        else{
                            sourcePattern.setRotation(Rotation.SOUTH);
                        }
                    }
                    else if(indexColumn == 0 && indexRow == parking.getHorizontalSize() - 1){
                        if(currentRotation == Rotation.NORTH){
                            sourcePattern.setRotation(Rotation.EAST);
                        }
                        else{
                            sourcePattern.setRotation(Rotation.NORTH);
                        }
                    }
                    else if(indexColumn == parking.getVerticalSize() - 1 && indexRow == parking.getHorizontalSize() - 1){
                        if(currentRotation == Rotation.NORTH){
                            sourcePattern.setRotation(Rotation.WEST);
                        }
                        else{
                            sourcePattern.setRotation(Rotation.NORTH);
                        }
                    }
                    updateParking();
                }
                else if(event.getClickCount() == 2 && sourcePattern.getPatternType() == PatternType.OUT){
                    Rotation currentRotation = sourcePattern.getRotation();
                    if(indexRow == 0 && indexColumn == 0){
                        if(currentRotation == Rotation.NORTH){
                            sourcePattern.setRotation(Rotation.WEST);
                        }
                        else{
                            sourcePattern.setRotation(Rotation.NORTH);
                        }
                    }
                    else if(indexRow == 0 && indexColumn == parking.getVerticalSize() - 1){
                        if(currentRotation == Rotation.NORTH){
                            sourcePattern.setRotation(Rotation.EAST);
                        }
                        else{
                            sourcePattern.setRotation(Rotation.NORTH);
                        }
                    }
                    else if(indexColumn == 0 && indexRow == parking.getHorizontalSize() - 1){
                        if(currentRotation == Rotation.SOUTH){
                            sourcePattern.setRotation(Rotation.WEST);
                        }
                        else{
                            sourcePattern.setRotation(Rotation.SOUTH);
                        }
                    }
                    else if(indexColumn == parking.getVerticalSize() - 1 && indexRow == parking.getHorizontalSize() - 1){
                        if(currentRotation == Rotation.SOUTH){
                            sourcePattern.setRotation(Rotation.EAST);
                        }
                        else{
                            sourcePattern.setRotation(Rotation.SOUTH);
                        }
                    }
                    updateParking();
                }
            }
        }));
    }

    //возврат списка доступных клеток для установки второй части грузовика
    private ArrayList<PairIJ> getIndexNearCellsTruck(int currentI, int currentJ){
        ArrayList<PairIJ> listIndex = new ArrayList<>();
        if(currentI != parking.getHorizontalSize() - 1){
            if(parking.getParkingCells()[currentI + 1][currentJ].getPattern().getPatternType() == PatternType.EMPTY){
                listIndex.add(new PairIJ(currentI + 1, currentJ));
            }
        }
        if(currentI != 0){
            if(parking.getParkingCells()[currentI - 1][currentJ].getPattern().getPatternType() == PatternType.EMPTY){
                listIndex.add(new PairIJ(currentI - 1, currentJ));
            }
        }
        if(currentJ != parking.getVerticalSize() - 1){
            if(parking.getParkingCells()[currentI][currentJ + 1].getPattern().getPatternType() == PatternType.EMPTY){
                listIndex.add(new PairIJ(currentI, currentJ + 1));
            }
        }
        if(currentJ != 0){
            if(parking.getParkingCells()[currentI][currentJ - 1].getPattern().getPatternType() == PatternType.EMPTY){
                listIndex.add(new PairIJ(currentI, currentJ - 1));
            }
        }
        return listIndex;
    }

    private ArrayList<PairIJ> getIndexNearCells(int currentI, int currentJ){
        ArrayList<PairIJ> listIndex = new ArrayList<>();
        if(currentI != parking.getHorizontalSize() - 1){
            listIndex.add(new PairIJ(currentI + 1, currentJ));
        }
        if(currentI != 0){
            listIndex.add(new PairIJ(currentI - 1, currentJ));
        }
        if(currentJ != parking.getVerticalSize() - 1){
            listIndex.add(new PairIJ(currentI, currentJ + 1));
        }
        if(currentJ != 0){
            listIndex.add(new PairIJ(currentI, currentJ - 1));
        }
        return listIndex;
    }

    private void rotateINOUT(Pattern pattern, int indexRow, int indexColumn){
        if(pattern.getPatternType() == PatternType.IN){
            if(indexRow == 0){
                pattern.setRotation(Rotation.SOUTH);
            }
            else if(indexRow == parking.getHorizontalSize() - 1){
                pattern.setRotation(Rotation.NORTH);
            }
            else if(indexColumn == 0){
                pattern.setRotation(Rotation.EAST);
            }
            else if(indexColumn == parking.getVerticalSize() - 1){
                pattern.setRotation(Rotation.WEST);
            }
        }
        else {
            if(indexRow == 0){
                pattern.setRotation(Rotation.NORTH);
            }
            else if(indexRow == parking.getHorizontalSize() - 1){
                pattern.setRotation(Rotation.SOUTH);
            }
            else if(indexColumn == 0){
                pattern.setRotation(Rotation.WEST);
            }
            else if(indexColumn == parking.getVerticalSize() - 1){
                pattern.setRotation(Rotation.EAST);
            }
        }
    }

    //координаты парковки в индекс gridPane
    private int coordinatesToIndex(int i, int j){
        return (i * parking.getVerticalSize() + j + 1);
    }

    @FXML //выбрана машина
    private void clickCar(){
        isRed = false;
        isBlue = false;
        isGreen = true;
        checkHighlightIcons();
        checkHighLightChildren();
        car.setEffect(getShadowEffect(Color.GREEN)); //view
        currentImageView = car;
        currentPattern = new Pattern(PatternType.CAR);
    }

    @FXML //выбран выезд
    private void clickArrowUp(){
        isRed = false;
        isBlue = false;
        isGreen = true;
        checkHighlightIcons();
        checkHighLightChildren();
        arrowOut.setEffect(getShadowEffect(Color.GREEN)); //view
        currentImageView = arrowOut;
        currentPattern = new Pattern(PatternType.OUT);
    }

    @FXML // выбран въезд
    private void clickArrowDown(){
        isRed = false;
        isBlue = false;
        isGreen = true;
        checkHighlightIcons();
        checkHighLightChildren();
        arrowIn.setEffect(getShadowEffect(Color.GREEN)); //view
        currentImageView = arrowIn;
        currentPattern = new Pattern(PatternType.IN);
    }

    @FXML // выбрана касса
    private void clickCash(){
        isRed = false;
        isBlue = false;
        isGreen = true;
        checkHighlightIcons();
        checkHighLightChildren();
        cash.setEffect(getShadowEffect(Color.GREEN)); //view
        currentImageView = cash;
        currentPattern = new Pattern(PatternType.CASH);
    }

    @FXML //выбрана дорога
    private void clickRoad(){
        isRed = false;
        isBlue = false;
        isGreen = true;
        checkHighlightIcons();
        checkHighLightChildren();
        road.setEffect(getShadowEffect(Color.GREEN)); //view
        currentImageView = road;
        currentPattern = new Pattern(PatternType.ROAD);
    }

    @FXML // выбран грузовик
    private void clickTruck(){
        isRed = false;
        isBlue = false;
        isGreen = true;
        checkHighlightIcons();
        checkHighLightChildren();
        truck.setEffect(getShadowEffect(Color.GREEN)); //view
        currentImageView = truck;
        currentPattern = new Pattern(PatternType.TRUCK_HEAD);
    }

    @FXML // выбран крест удаления
    private void clickCross(){
        isRed = true;
        isBlue = false;
        isGreen = false;
        checkHighlightIcons();
        checkHighLightChildren();;
        cross.setEffect(getShadowEffect(Color.RED)); //view
        currentImageView = emptyPattern;
        currentPattern = new Pattern(PatternType.EMPTY);
    }

    //проверка наличия только одного выделенного шаблона //view
    private void checkHighlightIcons(){
        for(ImageView elem: imageViews){
            if(elem.getEffect() instanceof InnerShadow){
                elem.setEffect(new Blend());
            }
        }
    }

    //проверка наличия только одной выделенной клетки на парковке //view
    private void checkHighLightChildren(){
        ObservableList<Node> observableList = gridPane.getChildren();
        int size = observableList.size();
        for(int i = 1; i < size; i++){
            ImageView imageView = (ImageView)observableList.get(i);
            if(imageView.getEffect() instanceof InnerShadow || imageView.getEffect() instanceof Bloom){
                imageView.setEffect(new Blend());
            }
        }
    }

    //проверка наличия только одного въезда, выезда и кассы
    private void checkUniqueIcons(){
        int countCash = 0;
        int countArrowIn = 0;
        int countArrowOut = 0;
        for(int i = 0; i < parking.getHorizontalSize(); i++){
            for(int j = 0; j < parking.getVerticalSize(); j++){
                if(parking.getParkingCells()[i][j].getPattern().getPatternType() == PatternType.CASH){
                    cash.setDisable(true); //view
                    cash.setEffect(new Blend()); //view
                    if(currentPattern.getPatternType() == PatternType.CASH) {
                        currentImageView = emptyPattern;
                        currentPattern = new Pattern(PatternType.EMPTY);
                    }
                    countCash++;
                }
                else if(parking.getParkingCells()[i][j].getPattern().getPatternType() == PatternType.IN){
                    arrowIn.setDisable(true); //view
                    arrowIn.setEffect(new Blend()); //view
                    if(currentPattern.getPatternType() == PatternType.IN) {
                        currentImageView = emptyPattern;
                        currentPattern = new Pattern(PatternType.EMPTY);
                    }
                    countArrowIn++;
                }
                else if(parking.getParkingCells()[i][j].getPattern().getPatternType() == PatternType.OUT){
                    arrowOut.setDisable(true); //view
                    arrowOut.setEffect(new Blend()); //view
                    if(currentPattern.getPatternType() == PatternType.OUT) {
                        currentImageView = emptyPattern;
                        currentPattern = new Pattern(PatternType.EMPTY);
                    }
                    countArrowOut++;
                }
            }
        }
        if(countCash == 0){
            cash.setDisable(false); //view
        }
        if(countArrowOut == 0){
            arrowOut.setDisable(false); //view
        }
        if(countArrowIn == 0){
            arrowIn.setDisable(false); //view
        }
    }

    //удаление всех ячеек(перед изменением размера парковки) //view
    private void removeAllChildren(){
        ObservableList<Node> observableList = gridPane.getChildren();
        int size = observableList.size();
        if (size > 1) {
            observableList.subList(1, size).clear();
        }
    }
    //проверка на возможность изменять размер парковки
    private boolean ableChangeSize(){
        for(int i = 0; i < parking.getHorizontalSize(); i++){
            for(int j = 0; j < parking.getVerticalSize(); j++){
                if(parking.getParkingCells()[i][j].getPattern().getPatternType() != PatternType.EMPTY){
                    return false;
                }
            }
        }
        return true;
    }
    //блокировка спиннеров при запрете изменения
    private void checkChangeSpinnerSize(){ //view
        if(!ableChangeSize()){
            spinnerHorizontal.setDisable(true);
            spinnerVertical.setDisable(true);
        }
        else{
            spinnerHorizontal.setDisable(false);
            spinnerVertical.setDisable(false);
        }
    }

    @FXML
    private void saveFile() {
        if(checkPresenceUniqueElements()&&checkPlacementUniqueElements()&&checkRoads()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("src/parkings"));
            File file = fileChooser.showSaveDialog(new Stage());
            if (file != null) {
                String path = file.getPath();
                path += ".park";
                try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(parking);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Топология некорректна. Сохранение невозможно.");
            alert.showAndWait();
        }
    }

    @FXML
    private void loadFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src/parkings"));
        File file = fileChooser.showOpenDialog(new Stage());
        if(file != null) {
            int numPoint = file.getName().lastIndexOf('.');
            if(numPoint > 0 && file.getName().substring(numPoint+1).equals("park")) {
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    Parking readParking = null;
                    try {
                        readParking = (Parking) objectInputStream.readObject();
                    }
                    catch (ClassNotFoundException | ClassCastException ex){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка");
                        alert.setHeaderText(null);
                        alert.setContentText("Файл с топологией парковки поврежден! Открытие невозможно. ");
                        alert.showAndWait();
                        return;
                    }
                    int horizontalLength = readParking.getHorizontalSize();
                    int verticalLength = readParking.getVerticalSize();
                    int difHor = Math.abs(horizontalLength - spinnerHorizontal.getValue());
                    int difVer = Math.abs(verticalLength - spinnerVertical.getValue());
                    if(horizontalLength < spinnerHorizontal.getValue()){
                        for(int i = 0; i < difHor; i++){
                            spinnerHorizontal.getValueFactory().setValue(spinnerHorizontal.getValue() - 1);
                            changeHorizontalSize();
                        }
                    }
                    if(horizontalLength > spinnerHorizontal.getValue()){
                        for(int i = 0; i < difHor; i++){
                            spinnerHorizontal.getValueFactory().setValue(spinnerHorizontal.getValue() + 1);
                            changeHorizontalSize();
                        }
                    }
                    if(verticalLength < spinnerVertical.getValue()){
                        for(int i = 0; i < difVer; i++){
                            spinnerVertical.getValueFactory().setValue(spinnerVertical.getValue() - 1);
                            changeVerticalSize();
                        }
                    }
                    if(verticalLength > spinnerVertical.getValue()){
                        for(int i = 0; i < difVer; i++){
                            spinnerVertical.getValueFactory().setValue(spinnerVertical.getValue() + 1);
                            changeVerticalSize();
                        }
                    }
                    parking = readParking;
                    updateParking();
                    checkChangeSpinnerSize();
                    checkHighlightIcons();
                    checkHighLightChildren();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
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

    private ColorInput getWhiteColorEffect(double height, double width){
        ColorInput colorInput = new ColorInput();
        colorInput.setHeight(height);
        colorInput.setWidth(width);
        colorInput.setPaint(Color.WHITE);
        return colorInput;
    }

    private InnerShadow getShadowEffect(Color color){
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(color);
        return innerShadow;
    }

    private Bloom getBloomEffect(Color color){
        Bloom bloom = new Bloom(10);
        bloom.setInput(getShadowEffect(color));
        return bloom;
    }

    @FXML
    private void check(){
        boolean conditionOne = checkPresenceUniqueElements();
        boolean conditionTwo = checkPlacementUniqueElements();
        boolean conditionThree = checkRoads();
        if(conditionOne&&conditionTwo&&conditionThree) {
            Alert alertWindow = new Alert(Alert.AlertType.INFORMATION);
            alertWindow.setTitle("Сообщение");
            alertWindow.setHeaderText(null);
            alertWindow.setContentText("Топология корректна!");
            alertWindow.showAndWait();
        }
        else {
            Alert alertWindow = new Alert(Alert.AlertType.ERROR);
            alertWindow.setTitle("Ошибка");
            alertWindow.setHeaderText(null);
            String message = "Топология не корректна! \n";
            if(!conditionOne) {
                message += "Въезд, выезд и/или касса не установлены! \n";
            }
            if(conditionOne&&!conditionTwo) {
                message += "Въезд, выезд или касса установлены неправильно. Установите их на граничных клетках парковки! Касса должна примыкать к въезду или выезду \n\n";
            }
            if(conditionOne&&!conditionThree) {
                message += "На парковке нет парковочных мест ИЛИ \nСуществуют места на парковке не соединенные дорогами с въездом/выездом! \n";
            }
            alertWindow.setContentText(message);
            alertWindow.showAndWait();
        }
    }

    //проверка наличия въезда, выезда, кассы
    private boolean checkPresenceUniqueElements(){
        int countCash = 0;
        int countArrowIn = 0;
        int countArrowOut = 0;
        for(int i = 0; i < parking.getHorizontalSize(); i++){
            for(int j = 0; j < parking.getVerticalSize(); j++){
                ParkingCell parkingCell = parking.getParkingCells()[i][j];
                if(parkingCell.getPattern().getPatternType() == PatternType.CASH){
                    countCash++;
                }
                else if(parkingCell.getPattern().getPatternType() == PatternType.IN){
                    countArrowIn++;
                }
                else if(parkingCell.getPattern().getPatternType() == PatternType.OUT){
                    countArrowOut++;
                }
            }
        }
        return countCash != 0 && countArrowIn != 0 && countArrowOut != 0;
    }

    //проверка расположения въезда, выезда, кассы
    private boolean checkPlacementUniqueElements(){
        int rowIn = 0;
        int columnIn = 0;
        int rowOut = 0;
        int columnOut = 0;
        int rowCash = 0;
        int columnCash = 0;
        for(int i = 0; i < parking.getHorizontalSize(); i++) {
            for (int j = 0; j < parking.getVerticalSize(); j++) {
                ParkingCell parkingCell = parking.getParkingCells()[i][j];
                if ((parkingCell.getPattern().getPatternType() == PatternType.IN || parkingCell.getPattern().getPatternType() == PatternType.OUT || parkingCell.getPattern().getPatternType() == PatternType.CASH)) {
                    if (i != 0 && j != 0 && i != parking.getHorizontalSize() - 1 && j != parking.getVerticalSize() - 1) {
                        gridPane.getChildren().get(coordinatesToIndex(i, j)).setEffect(getShadowEffect(Color.RED));
                        return false;
                    }
                    if (parkingCell.getPattern().getPatternType() == PatternType.IN) {
                        rowIn = parkingCell.getCoordinateHorizontal();
                        columnIn = parkingCell.getCoordinateVertical();
                    }
                    else if (parkingCell.getPattern().getPatternType() == PatternType.OUT) {
                        rowOut = parkingCell.getCoordinateHorizontal();
                        columnOut = parkingCell.getCoordinateVertical();
                    }
                    else if(parkingCell.getPattern().getPatternType() == PatternType.CASH){
                        rowCash = parkingCell.getCoordinateHorizontal();
                        columnCash = parkingCell.getCoordinateVertical();
                    }
                }
            }
        }
        if(rowIn != rowOut && columnIn != columnOut){
            gridPane.getChildren().get(coordinatesToIndex(rowOut, columnOut)).setEffect(getShadowEffect(Color.RED));
            return false;
        }
        switch (parking.getParkingCells()[rowIn][columnIn].getPattern().getRotation()){
            case SOUTH:{
                if(parking.getParkingCells()[rowOut][columnOut].getPattern().getRotation() != Rotation.NORTH){
                    gridPane.getChildren().get(coordinatesToIndex(rowOut, columnOut)).setEffect(getShadowEffect(Color.RED));
                    return false;
                }
                break;
            }
            case NORTH:{
                if(parking.getParkingCells()[rowOut][columnOut].getPattern().getRotation() != Rotation.SOUTH){
                    gridPane.getChildren().get(coordinatesToIndex(rowOut, columnOut)).setEffect(getShadowEffect(Color.RED));
                    return false;
                }
                break;
            }
            case WEST:{
                if(parking.getParkingCells()[rowOut][columnOut].getPattern().getRotation() != Rotation.EAST){
                    gridPane.getChildren().get(coordinatesToIndex(rowOut, columnOut)).setEffect(getShadowEffect(Color.RED));
                    return false;
                }
                break;
            }
            case EAST:{
                if(parking.getParkingCells()[rowOut][columnOut].getPattern().getRotation() != Rotation.WEST){
                    gridPane.getChildren().get(coordinatesToIndex(rowOut, columnOut)).setEffect(getShadowEffect(Color.RED));
                    return false;
                }
                break;
            }
        }
        ArrayList<PairIJ> listNearIn = getIndexNearCells(rowIn, columnIn);
        ArrayList<PairIJ> listNearOut = getIndexNearCells(rowOut, columnOut);
        for(PairIJ pair: listNearIn){
            if(parking.getParkingCells()[pair.getI()][pair.getJ()].getPattern().getPatternType() == PatternType.CASH){
                return true;
            }
        }
        for(PairIJ pair: listNearOut){
            if(parking.getParkingCells()[pair.getI()][pair.getJ()].getPattern().getPatternType() == PatternType.CASH){
                return true;
            }
        }
        gridPane.getChildren().get(coordinatesToIndex(rowCash, columnCash)).setEffect(getShadowEffect(Color.RED));
        return false;
    }

    private boolean checkRoads(){
        int rowIn = 0;
        int columnIn = 0;
        int rowOut = 0;
        int columnOut = 0;
        int countPlacesStay = 0;
        for(int i = 0; i < parking.getHorizontalSize(); i++) {
            for (int j = 0; j < parking.getVerticalSize(); j++) {
                ParkingCell parkingCell = parking.getParkingCells()[i][j];
                if(parkingCell.getPattern().getPatternType() == PatternType.IN){
                    rowIn = i;
                    columnIn = j;
                }
                else if(parkingCell.getPattern().getPatternType() == PatternType.OUT){
                    rowOut = i;
                    columnOut = j;
                }
            }
        }
        for(int i = 0; i < parking.getHorizontalSize(); i++) {
            for (int j = 0; j < parking.getVerticalSize(); j++) {
                ParkingCell parkingCell = parking.getParkingCells()[i][j];
                if(parkingCell.getPattern().getPatternType() == PatternType.CAR || parkingCell.getPattern().getPatternType() == PatternType.TRUCK_HEAD){
                    countPlacesStay++;
                    WaveAlg path1 = new WaveAlg(getMatrixParking(new PairIJ(rowIn, columnIn), new PairIJ(i, j)));
                    path1.findPath(columnIn + 1, rowIn + 1,j+1, i+1);
                    if(path1.getWave().size() == 1){
                        gridPane.getChildren().get(coordinatesToIndex(i, j)).setEffect(getShadowEffect(Color.RED));
                        return false;
                    }
                    WaveAlg path2 = new WaveAlg(getMatrixParking(new PairIJ(rowOut, columnOut), new PairIJ(i, j)));
                    path2.findPath(j + 1, i + 1,columnOut+1, rowOut+1);
                    if(path2.getWave().size() == 1){
                        gridPane.getChildren().get(coordinatesToIndex(i, j)).setEffect(getShadowEffect(Color.RED));
                        return false;
                    }
                }
            }
        }
        return countPlacesStay != 0;
    }

    private int[][] getMatrixParking(PairIJ pairOne, PairIJ pairTwo){
        int[][] matrix = new int[parking.getHorizontalSize()+2][parking.getVerticalSize() + 2];
        for (int i = 0; i < matrix.length; i++) {
            matrix[i][0] = 99;
            matrix[matrix.length - 1][i] = 99;
        }
        for (int i = 0; i < matrix[0].length; i++) {
            matrix[0][i] = 99;
            matrix[i][matrix[0].length - 1] = 99;
        }
        for(int i = 0; i < parking.getHorizontalSize(); i++){
            for(int j = 0; j < parking.getVerticalSize(); j++){
                ParkingCell parkingCell = parking.getParkingCells()[i][j];
                if(parkingCell.getPattern().getPatternType() == PatternType.ROAD){
                    matrix[i+1][j+1] = -1;
                }
                else if(i == pairTwo.getI() && j == pairTwo.getJ()){
                    matrix[i+1][j+1] = -1;
                }
                else if(i == pairOne.getI() && j == pairOne.getJ()){
                    matrix[i+1][j+1] = -1;
                }
                else {
                    matrix[i+1][j+1] = 99;
                }
            }
        }
        return matrix;
    }

    @FXML
    private void toModelling() throws IOException{
        if(checkPresenceUniqueElements()&&checkPlacementUniqueElements()&&checkRoads()) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxmls/modelling.fxml"));
            fxmlLoader.setControllerFactory(c -> new ModellingController(parking));
            Parent modellingWindow = fxmlLoader.load();
            stage.setTitle("Моделирование");
            stage.setScene(new Scene(modellingWindow, 600, 400));
            stage.show();
        }
        else {
            Alert alertWindow = new Alert(Alert.AlertType.ERROR);
            alertWindow.setTitle("Ошибка");
            alertWindow.setHeaderText(null);
            alertWindow.setContentText("Топология некорректна. Переход в режим моделирования невозможен");
            alertWindow.showAndWait();
        }
    }

    @FXML
    private void infoSystem() throws URISyntaxException, IOException {
        URI uri = new URI("http://localhost:63342/Parking_App/ParkingApp/sample/web/infoSystem.html?_ijt=9b9p3l11qvg3qmuumq818hopi8");
        java.awt.Desktop.getDesktop().browse(uri);
    }

    @FXML
    private void infoCreators() throws IOException{
        Parent helpWindow = FXMLLoader.load(getClass().getResource("../fxmls/infocreators.fxml"));
        Stage helpStage = new Stage();
        helpStage.setTitle("О разработчиках");
        helpStage.setTitle("О разработчиках");
        helpStage.setScene(new Scene(helpWindow, 600, 345));
        helpStage.showAndWait();
    }

    /*
    СДЕЛАТЬ:
    ограничение на въезд, выезд и кассу (СДЕЛАНО)
    сделать грузовик, который будет занимать 2 места (ГОТОВО!)
    блокировка счетчиков, если на поле есть не пустой шаблон (СДЕЛАНО)
    сделать удаление грузовика (СДЕЛАНО)
    сделать перемещение грузовика (СДЕЛАНО)
    сохранение в файл(корректного и некорректного) (КОРРЕКТНЫЙ СДЕЛАНО)
    загрузка из файла (СДЕЛАНО)
    проверка корректности
    вращение въезда, выезда (СДЕЛАНО)
    исправить размер элементов в клетках
     */

    /*
    ДЛЯ ТЕСТОВ:
    1. Добавление шаблонов из одной клетки
    2. Добавление уникальных шаблонов из одной клетки
    3. Добавление грузовиков в любой конфигурации
    4. Удаление уникальных и неуникальных шиблонов из одной клетки
    5. Удаление грузовиков
    6. Перемещение шаблонов из одной клетки
    7. Перемещение грузовиков любой конфигурации
    8. Проверка правильности конфигурации въезда, выезда
    9. Проверка корректности топологии
    10. Сохранение в файл
    11. Загрузка в файл
     */
}
