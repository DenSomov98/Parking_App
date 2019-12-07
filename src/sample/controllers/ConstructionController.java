package sample.controllers;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.enums.PatternType;
import sample.enums.Rotation;
import sample.models.Parking;
import sample.models.ParkingCell;
import sample.models.Pattern;

import java.io.*;
import java.util.ArrayList;

public class ConstructionController {
    private static Stage stage;
    private Parking parking;

    @FXML
    private ImageView car;
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

    private ImageView emptyPattern;
    private ImageView[] imageViews;
    private int countTrucks;
    private boolean isGreen; // выбран активный шаблон
    private boolean isRed; // выбран крест
    private boolean isBlue; // выбран шаблон для перемещения
    private boolean isPurple; // добавляется грузовик
    private boolean isMovedTruck; //перемещается грузовик
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
                imageView.setEffect(getWhiteColorEffect(imageView.getImage().getHeight(), imageView.getImage().getWidth()));
                GridPane.setHalignment(imageView, HPos.CENTER);
                GridPane.setValignment(imageView, VPos.CENTER);
                gridPane.add(imageView, j, i);
            }
        }
    }

    // рисование парковки (пустой)
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

    // обновление парковки (ОБЪЕДИНИТЬ ВЕТКИ)
    private void updateParking(){
        removeAllChildren();
        double height = gridPane.getHeight();
        double width = gridPane.getWidth();
        int max = Math.max(spinnerVertical.getValue(), spinnerHorizontal.getValue());
        for(int i = 0; i < parking.getHorizontalSize(); i++){
            for(int j = 0; j < parking.getVerticalSize(); j++){
                ParkingCell parkingCell = parking.getParkingCells()[i][j];
                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(parkingCell.getPattern().getPatternType().getPath())));
                if(parkingCell.getPattern().getPatternType() != PatternType.TRUCK_HEAD && parkingCell.getPattern().getPatternType() != PatternType.TRUCK_TAIL) {
                    imageView.setFitWidth(width / (max + 2));
                    imageView.setFitHeight(height / (max + 2));
                    imageView.setPreserveRatio(true);
                    if (parkingCell.getPattern().getPatternType() == PatternType.EMPTY) {
                        imageView.setEffect(getWhiteColorEffect(height / (max + 2), width / (max + 2)));
                    }
                    GridPane.setHalignment(imageView, HPos.CENTER);
                    GridPane.setValignment(imageView, VPos.CENTER);
                    GridPane.setMargin(imageView, new Insets(10));
                }
                else{
                    imageView.setFitWidth(width / (max + 2));
                    imageView.setFitHeight(height / (max + 2));
                    imageView.setPreserveRatio(true);
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
                    GridPane.setHalignment(imageView, HPos.CENTER);
                    GridPane.setValignment(imageView, VPos.CENTER);
                    GridPane.setMargin(imageView, new Insets(10, 10, 10, 10));
                }
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
            gridPane.getRowConstraints().add(rowConstraints);
        }
        else if(spinnerHorizontal.getValue() < lengthHorizontal){
            removeAllChildren();
            gridPane.getRowConstraints().remove(lengthHorizontal-1);
        }
        drawParking();
        addGridEvent();
    }

    @FXML // метод изменения размеров по вертикали
    private void changeVerticalSize(){
        int lengthVertical = gridPane.getColumnConstraints().size();
        ColumnConstraints columnConstraints = gridPane.getColumnConstraints().get(lengthVertical-1);
        if(spinnerVertical.getValue() > lengthVertical){
            gridPane.getColumnConstraints().add(columnConstraints);
        }
        else if(spinnerVertical.getValue() < lengthVertical){
            removeAllChildren();
            gridPane.getColumnConstraints().remove(lengthVertical-1);
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
                if(event.getButton() == MouseButton.PRIMARY && isGreen && !isBlue && currentPattern.getPatternType() != PatternType.EMPTY && currentPattern.getPatternType() != PatternType.TRUCK) {
                    parking.addPattern(indexRow, indexColumn, currentPattern);
                    updateParking();
                    checkChangeSpinnerSize();
                    checkUniqueIcons();
                }
                //СЛУЧАЙ ДОБАВЛЕНИЯ ПЕРЕДНЕЙ ЧАСТИ ГРУЗОВИКА (+)
                else if(event.getButton() == MouseButton.PRIMARY && isGreen && !isBlue && currentPattern.getPatternType() != PatternType.EMPTY && !isPurple){
                    isPurple = true;
                    parking.addPattern(indexRow, indexColumn, new Pattern(PatternType.TRUCK_HEAD));
                    source.setEffect(getBloomEffect(Color.ORANGE));
                    ArrayList<Integer> listIndexNearCells = getIndexNearCells(indexRow, indexColumn);
                    for(int index: listIndexNearCells){
                        ImageView imageView = (ImageView)gridPane.getChildren().get(index);
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
                    isBlue = false;
                    parking.addPattern(indexRow, indexColumn, chosenPattern);
                    parking.removePattern(indexRowChosen, indexColumnChosen);
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
            }
        }));
    }

    //возврат списка доступных клеток для установки второй части грузовика
    private ArrayList<Integer> getIndexNearCells(int currentI, int currentJ){
        ArrayList<Integer> listIndex = new ArrayList<>();
        if(currentI != parking.getHorizontalSize() - 1){
            if(parking.getParkingCells()[currentI + 1][currentJ].getPattern().getPatternType() == PatternType.EMPTY){
                listIndex.add(coordinatesToIndex(currentI + 1, currentJ));
            }
        }
        if(currentI != 0){
            if(parking.getParkingCells()[currentI - 1][currentJ].getPattern().getPatternType() == PatternType.EMPTY){
                listIndex.add(coordinatesToIndex(currentI - 1, currentJ));
            }
        }
        if(currentJ != parking.getVerticalSize() - 1){
            if(parking.getParkingCells()[currentI][currentJ + 1].getPattern().getPatternType() == PatternType.EMPTY){
                listIndex.add(coordinatesToIndex(currentI, currentJ + 1));
            }
        }
        if(currentJ != 0){
            if(parking.getParkingCells()[currentI][currentJ - 1].getPattern().getPatternType() == PatternType.EMPTY){
                listIndex.add(coordinatesToIndex(currentI, currentJ - 1));
            }
        }
        return listIndex;
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
        car.setEffect(getShadowEffect(Color.GREEN));
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
        arrowOut.setEffect(getShadowEffect(Color.GREEN));
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
        arrowIn.setEffect(getShadowEffect(Color.GREEN));
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
        cash.setEffect(getShadowEffect(Color.GREEN));
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
        road.setEffect(getShadowEffect(Color.GREEN));
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
        truck.setEffect(getShadowEffect(Color.GREEN));
        currentImageView = truck;
        currentPattern = new Pattern(PatternType.TRUCK);
    }

    @FXML // выбран крест удаления
    private void clickCross(){
        isRed = true;
        isBlue = false;
        isGreen = false;
        checkHighlightIcons();
        checkHighLightChildren();;
        cross.setEffect(getShadowEffect(Color.RED));
        currentImageView = emptyPattern;
        currentPattern = new Pattern(PatternType.EMPTY);
    }

    //проверка наличия только одного выделенного шаблона
    private void checkHighlightIcons(){
        for(ImageView elem: imageViews){
            if(elem.getEffect() instanceof InnerShadow){
                elem.setEffect(new Blend());
            }
        }
    }

    //проверка наличия только одной выделенной клетки на парковке
    private void checkHighLightChildren(){
        ObservableList<Node> observableList = gridPane.getChildren();
        int size = observableList.size();
        for(int i = 1; i < size; i++){
            ImageView imageView = (ImageView)observableList.get(i);
            if(imageView.getEffect() instanceof InnerShadow && imageView.getImage() != truck.getImage()){
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
                    cash.setDisable(true);
                    cash.setEffect(new Blend());
                    if(currentPattern.getPatternType() == PatternType.CAR) {
                        currentImageView = emptyPattern;
                        currentPattern = new Pattern(PatternType.EMPTY);
                    }
                    countCash++;
                }
                else if(parking.getParkingCells()[i][j].getPattern().getPatternType() == PatternType.IN){
                    arrowIn.setDisable(true);
                    arrowIn.setEffect(new Blend());
                    if(currentPattern.getPatternType() == PatternType.IN) {
                        currentImageView = emptyPattern;
                        currentPattern = new Pattern(PatternType.EMPTY);
                    }
                    countArrowIn++;
                }
                else if(parking.getParkingCells()[i][j].getPattern().getPatternType() == PatternType.OUT){
                    arrowOut.setDisable(true);
                    arrowOut.setEffect(new Blend());
                    if(currentPattern.getPatternType() == PatternType.OUT) {
                        currentImageView = emptyPattern;
                        currentPattern = new Pattern(PatternType.EMPTY);
                    }
                    countArrowOut++;
                }
            }
        }
        if(countCash == 0){
            cash.setDisable(false);
        }
        if(countArrowOut == 0){
            arrowOut.setDisable(false);
        }
        if(countArrowIn == 0){
            arrowIn.setDisable(false);
        }
    }

    //удаление всех ячеек(перед изменением размера парковки)
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
    private void checkChangeSpinnerSize(){
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
                    Parking readParking = (Parking) objectInputStream.readObject();
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
                } catch (IOException | ClassNotFoundException ex) {
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
    private void test(){

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
    вращение въезда, выезда и кассы
     */
}
