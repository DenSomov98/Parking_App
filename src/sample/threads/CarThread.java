package sample.threads;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import sample.utils.LawDistribution;
import sample.utils.PairIJ;
import sample.utils.WaveAlg;
import sample.enums.PatternType;
import sample.models.Parking;
import sample.models.ParkingCell;
import sample.updaters.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

public class CarThread extends Thread{
    private AnchorPane anchorPane;
    private int number;
    private GridPane gridPane;
    private Label countNoFreePlaces;
    private volatile Label clock;
    private volatile Parking parking;
    private LawDistribution stayLawDistribution;
    private double probabilityIn;
    private volatile boolean canWork;
    private volatile double booster;
    private volatile boolean isSuspended;
    private volatile ArrayList<PairIJ> listCar;
    private volatile boolean isFinishedStaying;
    private volatile Calendar calendar;
    private volatile ImageView imageView;

    public CarThread(AnchorPane anchorPane, int number, GridPane gridPane, Label countNoFreePlaces, Parking parking, double booster, Calendar calendar, Label clock, LawDistribution stayLawDistribution, double probabilityIn){
        this.anchorPane = anchorPane;
        this.number = number;
        this.gridPane = gridPane;
        this.countNoFreePlaces = countNoFreePlaces;
        this.parking = parking;
        this.stayLawDistribution = stayLawDistribution;
        this.probabilityIn = probabilityIn;
        this.booster = booster;
        listCar = getListCar();
        canWork = true;
        isSuspended = false;
        this.calendar = calendar;
        this.clock = clock;
        this.imageView = new ImageView();
    }
    @Override
    public void run() {
        boolean isComeIn = false;
        PairIJ indexIn = getIndexIn();
        PairIJ indexOut = getIndexOut();
        int coordinateXIn = getCoordinateXOfIn();
        if(new Random().nextDouble() <= probabilityIn){
            if(listCar.size() != 0){
                isComeIn = true;
            }
        }
        for (int i = 0; i < anchorPane.getWidth() + 50; i += 10) {
            if(canWork) {
                Platform.runLater(new UpdaterCarInFlow(i, anchorPane, number));
                if(isComeIn && i > coordinateXIn){
                    try {
                        sleep((long) (100 / booster));
                    }
                    catch (InterruptedException ignored){
                        //ex.printStackTrace();
                    }
                    break;
                }
                else {
                    try {
                        sleep((long) (100 / booster));
                        if (isSuspended) {
                            synchronized (this) {
                                while (isSuspended) {
                                    wait();
                                }
                            }
                        }
                    } catch (InterruptedException ignored) {

                    }
                }
            }
        }
        if(isComeIn) { // подъезд к парковочному месту
            Random random = new Random();
            int index = random.nextInt(listCar.size());
            PairIJ pairChosen = listCar.get(index);
            System.out.println(clock.getText() +" Автомобиль " + number + " заехал на парковку и выбрал место " +  pairChosen.getI() + " " + pairChosen.getJ());
            parking.getParkingCells()[pairChosen.getI()][pairChosen.getJ()].setOccupied(true);
            int[][] matrixParking = getMatrixParking(indexIn,pairChosen);
            WaveAlg path = new WaveAlg(matrixParking);
            path.findPath(indexIn.getJ() + 1, indexIn.getI() + 1,pairChosen.getJ()+1, pairChosen.getI()+1);
            imageView = (ImageView)anchorPane.getChildren().get(number);
            Platform.runLater(new UpdateAnchorPane(number, anchorPane)); // вывод машины из транспортного потока
            for(int i = 0; i < path.getWave().size(); i++) { // заезд машины на парковку
                if (canWork) {
                    synchronized (parking.getParkingCells()[path.getWave().get(i).getI() - 1][path.getWave().get(i).getJ() - 1]) {
                        Platform.runLater(new UpdaterCarInParkingAdd(imageView, gridPane, path.getWave().get(i)));
                        try {
                            sleep((long) (500 / booster));
                            if (isSuspended) {
                                synchronized (this) {
                                    while (isSuspended) {
                                        wait();
                                    }
                                }
                            }
                        } catch (InterruptedException ignored) {

                        }
                        if (i != path.getWave().size() - 1) {
                            Platform.runLater(new UpdaterCarParkingRemove(gridPane, imageView));
                        }
                    }
                }
            }
            Platform.runLater(() -> countNoFreePlaces.setText(String.valueOf(Integer.parseInt(countNoFreePlaces.getText())+1)));
            int timeMin = 0; // расчет времени стоянки
            switch (stayLawDistribution.getLawType()){
                case UNIFORM:{
                    timeMin = getTimeUniform(stayLawDistribution.getParameterOne(), stayLawDistribution.getParameterTwo());
                    break;
                }
                case NORMAL:{
                    timeMin = getTimeNormal(stayLawDistribution.getParameterOne(), stayLawDistribution.getParameterTwo());
                    break;
                }
                case EXPONENTIAL:{
                    timeMin = getTimeExponential(stayLawDistribution.getParameterOne());
                    break;
                }
                case DETERMINE:{
                    timeMin = stayLawDistribution.getParameterOne();
                    break;
                }
            }
            System.out.println(clock.getText() + " Автомобиль " + number + " добрался до места " +  pairChosen.getI() + " " + pairChosen.getJ() + " и будет стоять " + timeMin + " минут");
            long millisBefore = calendar.getTimeInMillis();
            Calendar newCalendar = (GregorianCalendar)calendar.clone();
            newCalendar.add(Calendar.MINUTE, timeMin);
            long millisAfter = newCalendar.getTimeInMillis() - 1000;

            sleepCar(millisBefore, millisAfter);
            System.out.println(clock.getText() + " Автомобиль " + number + " проспал " + timeMin + " минут и направляется к выезду");
            matrixParking = getMatrixParking(pairChosen, getIndexOut());
            path = new WaveAlg(matrixParking);
            path.findPath(pairChosen.getJ() +1, pairChosen.getI() +1, indexOut.getJ() +1, indexOut.getI()+1);
            parking.getParkingCells()[pairChosen.getI()][pairChosen.getJ()].setOccupied(false);
            Platform.runLater(() -> countNoFreePlaces.setText(String.valueOf(Integer.parseInt(countNoFreePlaces.getText())-1)));
            for(int i = 0; i < path.getWave().size(); i++){ // отъезд машины из парковочного места
                if(canWork) {
                    synchronized (parking.getParkingCells()[path.getWave().get(i).getI() - 1][path.getWave().get(i).getJ() - 1]) {
                        if (i != 0) {
                            Platform.runLater(new UpdaterCarInParkingAdd(imageView, gridPane, path.getWave().get(i)));
                            //parking.getParkingCells()[path.getWave().get(i).getI() - 1][path.getWave().get(i).getJ() - 1].setOccupied(true);
                        }
                        try {
                            sleep((long) (500 / booster));
                            if (isSuspended) {
                                synchronized (this) {
                                    while (isSuspended) {
                                        wait();
                                    }
                                }
                            }
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        Platform.runLater(new UpdaterCarParkingRemove(gridPane, imageView));
                        //parking.getParkingCells()[path.getWave().get(i).getI() - 1][path.getWave().get(i).getJ() - 1].setOccupied(false);
                    }
                }
            }
            System.out.println(clock.getText() + " Автомобиль " + number + " покинул парковку");
        }
    }

    private void sleepCar(long millisBefore, long millisAfter) {
        try {
            long differenceMillis = millisAfter - millisBefore;
            sleep((long)(differenceMillis / booster)); //стоянка автомобиля в минутах
        }
        catch (InterruptedException ex) {
            if (isSuspended) {
                synchronized (this) {
                    while (isSuspended) {
                        try {
                            wait();
                        }
                        catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
            long currentMillis = calendar.getTimeInMillis();
            if(currentMillis < millisAfter) {
                sleepCar(currentMillis, millisAfter);
            }
        }
    }

    @Override
    public void interrupt() { // пауза потока
        isSuspended = true;
        super.interrupt();
    }

    public void finish() {
        canWork = false;
        isSuspended = true;
        super.interrupt();
    }

    public void setCanWork(boolean canWork){
        this.canWork = canWork;
    }

    public void setBooster(double booster) {
        this.booster = booster;
    }

    public synchronized void pauseThread(){ // приостановка потока
        isSuspended = true;
    }

    public synchronized void resumeThread(){ // запуск потока после приостановки
        isSuspended = false;
        notify();
    }

    private ArrayList<PairIJ> getListCar(){
        ArrayList<PairIJ> listCar = new ArrayList<>();
        for(int k = 0; k < parking.getHorizontalSize(); k++) {
            for (int j = 0; j < parking.getVerticalSize(); j++) {
                if (parking.getParkingCells()[k][j].getPattern().getPatternType() == PatternType.CAR && !parking.getParkingCells()[k][j].isOccupied()) {
                    listCar.add(new PairIJ(k, j));
                }
            }
        }
        return listCar;
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

    private PairIJ getIndexOut(){
        for(int i = 0; i < parking.getHorizontalSize(); i++){
            for(int j = 0; j < parking.getVerticalSize(); j++){
                if(parking.getParkingCells()[i][j].getPattern().getPatternType() == PatternType.OUT){
                    return new PairIJ(i, j);
                }
            }
        }
        return null;
    }

    private PairIJ getIndexIn(){
        for(int i = 0; i < parking.getHorizontalSize(); i++){
            for(int j = 0; j < parking.getVerticalSize(); j++){
                if(parking.getParkingCells()[i][j].getPattern().getPatternType() == PatternType.IN){
                    return new PairIJ(i, j);
                }
            }
        }
        return null;
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
    }
    private int getCoordinateYOfIn(){
        for(int i = 0; i < parking.getHorizontalSize(); i++){
            for(int j = 0; j < parking.getVerticalSize(); j++){
                if(parking.getParkingCells()[i][j].getPattern().getPatternType() == PatternType.IN){
                    int index = coordinatesToIndex(i, j);
                    return (int)gridPane.getChildren().get(index).getBoundsInParent().getMaxY();
                }
            }
        }
        return -1;
    }

    private InnerShadow getShadowEffect(Color color){
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(color);
        return innerShadow;
    }

    private int coordinatesToIndex(int i, int j){
        return (i * parking.getVerticalSize() + j + 1);
    }

    private int getTimeUniform(int a, int b){
        Random random = new Random();
        return a + random.nextInt(b-a+1);
    }

    private int getTimeExponential(int lambda){
        Random random = new Random();
        return (int)(-1* Math.log(random.nextDouble())/lambda);
    }

    private int getTimeNormal(int mathExpectation, int variance){
        return (int) (Math.sqrt(variance)*getTimeNormalDefault() + mathExpectation);
    }

    private double getTimeNormalDefault(){
        Random random = new Random();
        return random.nextGaussian();
    }
}
