package sample.controllers;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.Blend;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.MalformedURLException;

public class ConstructionController {
    private static Stage stage;
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
    private ImageView emptyPattern;
    private ImageView[] imageViews;
    private boolean isGreen; // выбран активный шаблон
    private boolean isRed; // выбран крест
    private boolean isBlue; // выбран шаблон для перемещения
    private boolean isPurple; // добавляется грузовик

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

    private ImageView currentImageView; // текущее изображение
    private ImageView chosenImageView; // выбранное изображение
    private Color colorTruck; //цвет выделения грузовика

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        ConstructionController.stage = stage;
    }

    @FXML
    private void initialize() throws MalformedURLException {
        imageViews = new ImageView[]{car, arrowIn, arrowOut, cash, road, truck, emptyPattern, cross};
        emptyPattern.setDisable(true);
        emptyPattern.setVisible(false);
        drawGridInit(); // изначальное рисование парковки
        addGridEvent(); // добавление методов обработки нажатий мыши на каждую ячейку
    }

    @FXML // метод изменения размера по горизонтали
    private void changeHorizontalSize() throws MalformedURLException{
        int lengthHorizontal = gridPane.getRowConstraints().size();
        RowConstraints rowConstraints = gridPane.getRowConstraints().get(lengthHorizontal-1);
        if((int) spinnerHorizontal.getValue() > lengthHorizontal){
            gridPane.getRowConstraints().add(rowConstraints);
        }
        else if((int) spinnerHorizontal.getValue() < lengthHorizontal){
            removeAllChildren();
            gridPane.getRowConstraints().remove(lengthHorizontal-1);
        }
        drawGrid();
        addGridEvent();
    }
    @FXML // метод изменения размеров по вертикали
    private void changeVerticalSize() throws MalformedURLException{
        int lengthVertical = gridPane.getColumnConstraints().size();
        ColumnConstraints columnConstraints = gridPane.getColumnConstraints().get(lengthVertical-1);
        if((int) spinnerVertical.getValue() > lengthVertical){
            gridPane.getColumnConstraints().add(columnConstraints);
        }
        else if((int) spinnerVertical.getValue() < lengthVertical){
            removeAllChildren();
            gridPane.getColumnConstraints().remove(lengthVertical-1);
        }
        drawGrid();
        addGridEvent();
    }
    //обработчик нажатия мыши на каждую из клеток парковки
    private void addGridEvent(){
        gridPane.getChildren().forEach(item-> item.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int truckIndex = 0;
                ImageView source = (ImageView) event.getSource();
                if(event.getButton() == MouseButton.PRIMARY&&isGreen&&!isBlue&&currentImageView.getImage() != emptyPattern.getImage()&&currentImageView.getImage() != truck.getImage()) {
                    source.setEffect(new Blend());
                    source.setImage(currentImageView.getImage());
                    if(currentImageView.getImage() == cash.getImage()||currentImageView.getImage() == arrowIn.getImage() ||currentImageView.getImage() == arrowOut.getImage()){
                        checkUniqueIcons();
                    }
                    checkChangeSpinnerSize();
                    //checkUniqueIcons();
                }
                else if(event.getButton() == MouseButton.PRIMARY&&isGreen&&!isBlue&&currentImageView.getImage() != emptyPattern.getImage()&&currentImageView.getImage() == truck.getImage()&&!isPurple){
                    source.setEffect(new Blend());
                    isPurple = true;
                    ObservableList<Node> observableList = gridPane.getChildren();
                    boolean isOk=false;
                    int rowIndex = GridPane.getRowIndex(source) + 1;
                    int columnIndex = GridPane.getColumnIndex(source) + 1;
                    int countRows = gridPane.getRowConstraints().size();
                    //int columns = gridPane.getColumnCount();
                    int countColumns = gridPane.getColumnConstraints().size();
                    int index = (rowIndex - 1)*countColumns+columnIndex;
                    truckIndex = index;
                    ImageView[] checkCells = null;
                    InnerShadow innerShadow = new InnerShadow();
                    innerShadow.setColor(Color.PURPLE);
                    if (rowIndex==1) {
                        if (columnIndex == 1) {
                            checkCells = new ImageView[]{(ImageView) observableList.get(index + 1),
                                    (ImageView) observableList.get(index + countColumns)};
                            for(ImageView imageView: checkCells) {
                                if (imageView.getImage() == emptyPattern.getImage()) {
                                    isOk = true;
                                }
                            }
                        }
                        if (columnIndex == countColumns){
                            checkCells = new ImageView[]{(ImageView) observableList.get(index - 1),
                                    (ImageView) observableList.get(index + countColumns)};
                            for(ImageView imageView: checkCells) {
                                if (imageView.getImage() == emptyPattern.getImage()) {
                                    isOk = true;
                                }
                            }
                        }
                        if (columnIndex != countColumns&&columnIndex != 1){
                            checkCells = new ImageView[]{(ImageView) observableList.get(index - 1),
                                    (ImageView) observableList.get(index + 1),
                                    (ImageView) observableList.get(index + countColumns)};
                            for(ImageView imageView: checkCells) {
                                if (imageView.getImage() == emptyPattern.getImage()) {
                                    isOk = true;
                                }
                            }
                        }
                    }
                    if (rowIndex==countRows) {
                        if (columnIndex == 1) {
                            checkCells = new ImageView[]{(ImageView) observableList.get(index + 1),
                                    (ImageView) observableList.get(index - countColumns)};
                            for(ImageView imageView: checkCells) {
                                if (imageView.getImage() == emptyPattern.getImage()) {
                                    isOk = true;
                                }
                            }
                        }
                        if (columnIndex == countColumns){
                            checkCells = new ImageView[]{(ImageView) observableList.get(index - 1),
                                    (ImageView) observableList.get(index - countColumns)};
                            for(ImageView imageView: checkCells) {
                                if (imageView.getImage() == emptyPattern.getImage()) {
                                    isOk = true;
                                }
                            }
                        }
                        if (columnIndex != countColumns&&columnIndex != 1){
                            checkCells = new ImageView[]{(ImageView) observableList.get(index - 1),
                                    (ImageView) observableList.get(index + 1),
                                    (ImageView) observableList.get(index - countColumns)};
                            for(ImageView imageView: checkCells) {
                                if (imageView.getImage() == emptyPattern.getImage()) {
                                    isOk = true;
                                }
                            }
                        }
                    }
                    if (columnIndex==1) {
                        if (rowIndex != countRows&&rowIndex != 1){
                            checkCells = new ImageView[]{(ImageView) observableList.get(index + 1),
                                    (ImageView) observableList.get(index + countColumns),
                                    (ImageView) observableList.get(index - countColumns)};
                            for(ImageView imageView: checkCells) {
                                if (imageView.getImage() == emptyPattern.getImage()) {
                                    isOk = true;
                                }
                            }
                        }
                    }
                    if (columnIndex==countColumns) {
                        if (rowIndex != countRows&&rowIndex != 1){
                            checkCells = new ImageView[]{(ImageView) observableList.get(index - 1),
                                    (ImageView) observableList.get(index + countColumns),
                                    (ImageView) observableList.get(index - countColumns)};
                            for(ImageView imageView: checkCells) {
                                if (imageView.getImage() == emptyPattern.getImage()) {
                                    isOk = true;
                                }
                            }
                        }
                    }
                    if (rowIndex!=1&&rowIndex!=countRows&&columnIndex!=1&&columnIndex!=countColumns){
                        checkCells = new ImageView[]{(ImageView) observableList.get(index - 1),
                                (ImageView) observableList.get(index + 1),
                                (ImageView) observableList.get(index - countColumns),
                                (ImageView) observableList.get(index + countColumns)};
                        for(ImageView imageView: checkCells) {
                            if (imageView.getImage() == emptyPattern.getImage()) {
                                isOk = true;
                            }
                        }
                    }
                    if (isOk) {
                        InnerShadow innerShadowRandom = new InnerShadow();
                        colorTruck = Color.color(Math.random(), Math.random(), Math.random());
                        innerShadowRandom.setColor(colorTruck);
                        source.setImage(currentImageView.getImage());
                        source.setEffect(innerShadowRandom);
                        for (ImageView imageView : checkCells) {
                            if (imageView.getImage() == emptyPattern.getImage()) {
                                imageView.setEffect(innerShadow);
                            }
                        }
                        for (ImageView imageView : imageViews) {
                            if (imageView.getImage() != truck.getImage()) {
                                imageView.setDisable(true);
                            }
                        }
                    }
                    else {
                        isPurple=false;
                        ColorInput colorInput = new ColorInput();
                        colorInput.setHeight(source.getFitHeight());
                        colorInput.setWidth(source.getFitWidth());
                        colorInput.setPaint(Color.WHITE);
                        source.setEffect(colorInput);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);

                        alert.setTitle("Выбрано неподходящее поле");
                        alert.setHeaderText(null);
                        alert.setContentText("Выберите другое поле, если хотите поставить там парковочное место для грузовой машины.");
                        alert.showAndWait();
                    }
                    checkChangeSpinnerSize();;
                }
                else if(event.getButton() == MouseButton.PRIMARY&&isGreen&&!isBlue&&currentImageView.getImage() != emptyPattern.getImage()&&currentImageView.getImage() == truck.getImage()&&isPurple){
                    if(source.getEffect() instanceof InnerShadow && source.getImage() != truck.getImage()){
                        source.setImage(currentImageView.getImage());
                        InnerShadow innerShadow = new InnerShadow();
                        innerShadow.setColor(colorTruck);
                        source.setEffect(innerShadow);
                        ObservableList<Node> observableList = gridPane.getChildren();
                        for(int i = 1; i < gridPane.getChildren().size(); i++){
                            ImageView imageView = (ImageView)observableList.get(i);
                            if(imageView.getEffect() instanceof InnerShadow && imageView.getImage() != truck.getImage()){
                                ColorInput colorInput = new ColorInput();
                                colorInput.setHeight(imageView.getFitHeight());
                                colorInput.setWidth(imageView.getFitWidth());
                                colorInput.setPaint(Color.WHITE);
                                imageView.setEffect(colorInput);
                            }
                        }
                        checkUniqueIcons();
                        car.setDisable(false);
                        road.setDisable(false);
                        truck.setDisable(false);
                        cross.setDisable(false);
                        //emptyPattern.setDisable(false);
                        isPurple = false;
                        colorTruck = null;
                    }
                }
                else if(event.getButton() == MouseButton.PRIMARY&&isRed&&source.getImage() != truck.getImage()){
                    ColorInput colorInput = new ColorInput();
                    colorInput.setHeight(source.getFitHeight());
                    colorInput.setWidth(source.getFitWidth());
                    colorInput.setPaint(Color.WHITE);
                    source.setEffect(colorInput);
                    source.setImage(currentImageView.getImage());
                    checkChangeSpinnerSize();
                    checkUniqueIcons();
                }
                else if(event.getButton() == MouseButton.PRIMARY&&isRed&&source.getImage() == truck.getImage()){
                    InnerShadow effectSource = (InnerShadow) source.getEffect();
                    colorTruck = effectSource.getColor();
                    ColorInput colorInput = new ColorInput();
                    colorInput.setHeight(source.getFitHeight());
                    colorInput.setWidth(source.getFitWidth());
                    colorInput.setPaint(Color.WHITE);
                    source.setEffect(colorInput);
                    source.setImage(currentImageView.getImage());
                    for(int i = 1; i < gridPane.getChildren().size(); i++){
                        ImageView imageView = (ImageView) gridPane.getChildren().get(i);
                        if(imageView.getEffect() instanceof InnerShadow) {
                            InnerShadow innerShadow = (InnerShadow) imageView.getEffect();
                            if (innerShadow.getColor().equals(colorTruck)) {
                                imageView.setEffect(colorInput);
                                imageView.setImage(currentImageView.getImage());
                            }
                        }
                    }
                    checkChangeSpinnerSize();
                    checkUniqueIcons();
                }
                else if(event.getButton() == MouseButton.SECONDARY&&source.getImage() != emptyPattern.getImage()){
                    checkHighlightIcons();
                    checkHighLightChildren();
                    isBlue = true;
                    isRed = false;
                    isGreen = false;
                    chosenImageView = source;
                    InnerShadow innerShadow = new InnerShadow();
                    innerShadow.setColor(Color.BLUE);
                    source.setEffect(innerShadow);
                }
                else if(event.getButton() == MouseButton.PRIMARY&&isBlue){
                    isBlue = false;
                    source.setImage(chosenImageView.getImage());
                    source.setEffect(new Blend());
                    ColorInput colorInput = new ColorInput();
                    colorInput.setHeight(chosenImageView.getFitHeight());
                    colorInput.setWidth(chosenImageView.getFitWidth());
                    colorInput.setPaint(Color.WHITE);
                    chosenImageView.setEffect(colorInput);
                    chosenImageView = null;
                    checkHighLightChildren();
                }
            }
        }));
    }

    @FXML //выбрана машина
    private void clickCar(){
        isRed = false;
        isBlue = false;
        isGreen = true;
        checkHighlightIcons();
        checkHighLightChildren();
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.GREEN);
        car.setEffect(innerShadow);
        currentImageView = car;
    }
    @FXML //выбран выезд
    private void clickArrowUp(){
        isRed = false;
        isBlue = false;
        isGreen = true;
        checkHighlightIcons();
        checkHighLightChildren();
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.GREEN);
        arrowOut.setEffect(innerShadow);
        currentImageView = arrowOut;
    }
    @FXML // выбран въезд
    private void clickArrowDown(){
        isRed = false;
        isBlue = false;
        isGreen = true;
        checkHighlightIcons();
        checkHighLightChildren();
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.GREEN);
        arrowIn.setEffect(innerShadow);
        currentImageView = arrowIn;
    }
    @FXML // выбрана касса
    private void clickCash(){
        isRed = false;
        isBlue = false;
        isGreen = true;
        checkHighlightIcons();
        checkHighLightChildren();
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.GREEN);
        cash.setEffect(innerShadow);
        currentImageView = cash;
    }
    @FXML //выбрана дорога
    private void clickRoad(){
        isRed = false;
        isBlue = false;
        isGreen = true;
        checkHighlightIcons();
        checkHighLightChildren();
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.GREEN);
        road.setEffect(innerShadow);
        currentImageView = road;
    }
    @FXML // выбран грузовик
    private void clickTruck(){
        isRed = false;
        isBlue = false;
        isGreen = true;
        checkHighlightIcons();
        checkHighLightChildren();
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.GREEN);
        truck.setEffect(innerShadow);
        currentImageView = truck;
    }
    @FXML  // Todo: убрать этот метод
    private void clickEmpty(){
        isRed = false;
        isBlue = false;
        isGreen = true;
        checkHighlightIcons();
        checkHighLightChildren();
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.GREEN);
        emptyPattern.setEffect(innerShadow);
        currentImageView = emptyPattern;
    }
    @FXML // выбран крест удаления
    private void clickCross(){
        isRed = true;
        isBlue = false;
        isGreen = false;
        checkHighlightIcons();
        checkHighLightChildren();
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.RED);
        cross.setEffect(innerShadow);
        currentImageView = emptyPattern;
    }
    //проверка наличия только одной выделенного шаблона
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
        ObservableList<Node> observableList = gridPane.getChildren();
        int size = observableList.size();
        int countCash = 0;
        int countArrowIn = 0;
        int countArrowOut = 0;
        for(int i = 1; i < size; i++){
            ImageView imageView = (ImageView)observableList.get(i);
            if(imageView.getImage() == cash.getImage()){
                cash.setDisable(true);
                //isGreen = false;
                cash.setEffect(new Blend());
                currentImageView = emptyPattern;
                countCash++;
            }
            else if(imageView.getImage() == arrowIn.getImage()){
                arrowIn.setDisable(true);
                // isGreen = false;
                arrowIn.setEffect(new Blend());
                currentImageView = emptyPattern;
                countArrowIn++;
            }
            else if(imageView.getImage() == arrowOut.getImage()){
                arrowOut.setDisable(true);
                //isGreen = false;
                arrowOut.setEffect(new Blend());
                currentImageView = emptyPattern;
                countArrowOut++;
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
    //рисование парковки
    private void drawGrid() throws MalformedURLException{
        removeAllChildren();
        double height = gridPane.getHeight();
        double width = gridPane.getWidth();
        int max = Math.max((int)spinnerVertical.getValue(), (int)spinnerHorizontal.getValue());
        for(int i = 0; i < (int)spinnerHorizontal.getValue(); i++){
            for(int j = 0; j < (int)spinnerVertical.getValue(); j++){
                //File file = new File("C:/Users/Денис/IdeaProjects/ParkingApp/src/sample/empty.JPG");
                //String localPath = file.toURI().toURL().toString();
                //Image image = new Image(localPath);
                ImageView imageView = new ImageView();
                imageView.setImage(emptyPattern.getImage());
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
    //изначальное рисование парковки
    private void drawGridInit(){
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                /*File file = new File("C:/Users/Денис/IdeaProjects/ParkingApp/src/sample/empty.JPG");
                String localPath = file.toURI().toURL().toString();
                Image image = new Image(localPath);
                ImageView imageView = new ImageView(image);*/
                ImageView imageView = new ImageView();
                imageView.setImage(emptyPattern.getImage());
                imageView.setFitHeight((double)373/7);
                imageView.setFitWidth((double)347/7);
                ColorInput colorInput = new ColorInput();
                colorInput.setHeight(imageView.getImage().getHeight());
                colorInput.setWidth(imageView.getImage().getWidth());
                colorInput.setPaint(Color.WHITE);
                imageView.setEffect(colorInput);
                GridPane.setHalignment(imageView, HPos.CENTER);
                GridPane.setValignment(imageView, VPos.CENTER);
                gridPane.add(imageView, j, i);
            }
        }
    }
    //удаление всех ячеек(перед изменением размера парковки)
    private void removeAllChildren(){
        ObservableList<Node> observableList = gridPane.getChildren();
        int size = observableList.size();
        for(int i = 1; i < size; i++){
            observableList.remove(1);
        }
    }
    //проверка на возможность изменять размер парковки
    private boolean ableChangeSize(){
        ObservableList<Node> observableList = gridPane.getChildren();
        int size = observableList.size();
        for(int i = 1; i < size; i++){
            ImageView imageView = (ImageView)observableList.get(i);
            if(imageView.getImage() != emptyPattern.getImage()){
                return false;
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
        int rowCount = gridPane.getRowConstraints().size();
        int columnCount = gridPane.getColumnConstraints().size();
        int[][] imageViewMatrix = new int[rowCount][columnCount];
        int number = 1;
        for(int i = 0; i < rowCount; i++){
            for(int j = 0; j < columnCount; j++){
                int numberWrite = -1;
                ImageView imageView = (ImageView)gridPane.getChildren().get(number);
                if(imageView.getImage() == emptyPattern.getImage()){
                    numberWrite = 0;
                }
                else if(imageView.getImage() == car.getImage()){
                    numberWrite = 1;
                }
                else if(imageView.getImage() == arrowIn.getImage()){
                    numberWrite = 2;
                }
                else if(imageView.getImage() == arrowOut.getImage()){
                    numberWrite = 3;
                }
                else if(imageView.getImage() == cash.getImage()){
                    numberWrite = 4;
                }
                else if(imageView.getImage() == road.getImage()){
                    numberWrite = 5;
                }
                else if(imageView.getImage() == truck.getImage()){
                    numberWrite = 6;
                }
                number++;
                imageViewMatrix[i][j] = numberWrite;
            }
        }
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(new Stage());
        if(file != null){
            String path = file.getPath();
            path += ".park";
            try(FileOutputStream fileOutputStream = new FileOutputStream(path)){
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(imageViewMatrix);
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }
    @FXML
    private void loadFile(){
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());
        if(file != null){
            int numPoint = file.getName().lastIndexOf('.');
            if( numPoint > 0 && file.getName().substring(numPoint+1).equals("park")) {
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    int[][] imageViewMatrix = (int[][]) objectInputStream.readObject();
                    spinnerHorizontal.getValueFactory().setValue((int) imageViewMatrix.length);
                    spinnerVertical.getValueFactory().setValue((int) imageViewMatrix[0].length);
                    removeAllChildren();
                    drawGrid();
                    int number = 1;
                    for (int i = 0; i < imageViewMatrix.length; i++) {
                        for (int j = 0; j < imageViewMatrix[i].length; j++) {
                            ImageView imageView = (ImageView) gridPane.getChildren().get(number);
                            switch (imageViewMatrix[i][j]) {
                                case 1: {
                                    imageView.setImage(car.getImage());
                                    imageView.setEffect(new Blend());
                                    break;
                                }
                                case 2: {
                                    imageView.setImage(arrowIn.getImage());
                                    imageView.setEffect(new Blend());
                                    break;
                                }
                                case 3: {
                                    imageView.setImage(arrowOut.getImage());
                                    imageView.setEffect(new Blend());
                                    break;
                                }
                                case 4: {
                                    imageView.setImage(cash.getImage());
                                    imageView.setEffect(new Blend());
                                    break;
                                }
                                case 5: {
                                    imageView.setImage(road.getImage());
                                    imageView.setEffect(new Blend());
                                    break;
                                }
                                case 6: {
                                    imageView.setImage(truck.getImage());
                                    imageView.setEffect(new Blend());
                                    break;
                                }
                            }
                            number++;
                        }
                    }
                    addGridEvent();
                    checkUniqueIcons();
                    checkChangeSpinnerSize();
                    checkHighlightIcons();
                    checkHighLightChildren();
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
    @FXML
    private void test(){

    }

    /*
    СДЕЛАТЬ:
    ограничение на въезд, выезд и кассу (СДЕЛАНО)
    сделать грузовик, который будет занимать 2 места (ГОТОВО!)
    блокировка счетчиков, если на поле есть не пустой шаблон (СДЕЛАНО)
    сделать удаление грузовика (СДЕЛАНО)
    сделать перемещение грузовика
    сохранение в файл(корректного и некорректного) (КОРРЕКТНЫЙ СДЕЛАНО)
    загрузка из файла (СДЕЛАНО)
    проверка корректности
    сделать одинаковый цвет у грузовичка (СДЕЛАНО)
     */
}
