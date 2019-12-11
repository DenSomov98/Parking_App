package sample.threads;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import sample.PairIJ;
import sample.WaveAlg;
import sample.enums.PatternType;
import sample.models.Parking;
import sample.models.ParkingCell;
import sample.updaters.*;

import java.util.ArrayList;
import java.util.Random;

public class CarThread extends Thread{
    private AnchorPane anchorPane;
    private int number;
    private boolean canWork;
    private double booster;
    private volatile boolean isSuspended;
    private int coordinateXIn;
    private GridPane gridPane;
    private PairIJ indexIn;
    private volatile Parking parking;

    public CarThread(AnchorPane anchorPane, int number, double booster, int coordinateXIn, GridPane gridPane, PairIJ indexIn, Parking parking){
        this.anchorPane = anchorPane;
        this.number = number;
        this.booster = booster;
        canWork = true;
        isSuspended = false;
        this.coordinateXIn = coordinateXIn;
        this.gridPane = gridPane;
        this.indexIn = indexIn;
        this.parking = parking;
    }
    @Override
    public void run() {
        boolean isComeIn = false;
        double probabilityIn = 0.5;
        ArrayList<PairIJ> listCar = new ArrayList<>();
        if(new Random().nextDouble() <= probabilityIn){
            for(int k = 0; k < parking.getHorizontalSize(); k++){
                for(int j = 0; j < parking.getVerticalSize(); j++){
                    if(parking.getParkingCells()[k][j].getPattern().getPatternType() == PatternType.CAR && !parking.getParkingCells()[k][j].isOccupied()){
                        listCar.add(new PairIJ(k, j));
                    }
                }
            }
            if(listCar.size() != 0) {
                isComeIn = true;
            }
        }
        for (int i = 0; i < anchorPane.getWidth() + 50; i += 10) {
            if(canWork) {
                Platform.runLater(new UpdaterCarInFlow(i, anchorPane, number));
                if(isComeIn && i > coordinateXIn){
                    try {
                        sleep((long) (500 / booster));
                    }
                    catch (InterruptedException ex){
                        ex.printStackTrace();
                    }
                    break;
                }
                try {
                    sleep((long)(100/booster));
                    if(isSuspended){
                        synchronized (this){
                            while (isSuspended){
                                wait();
                            }
                        }
                    }
                }
                catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        if(isComeIn) {
            /*ArrayList<PairIJ> listCar = new ArrayList<>();
            for(int i = 0; i < parking.getHorizontalSize(); i++){
                for(int j = 0; j < parking.getVerticalSize(); j++){
                    if(parking.getParkingCells()[i][j].getPattern().getPatternType() == PatternType.CAR && !parking.getParkingCells()[i][j].isOccupied()){
                        listCar.add(new PairIJ(i, j));
                    }
                }
            }*/
            Random random = new Random();
            int index = random.nextInt(listCar.size());
            PairIJ pairChosen = listCar.get(index);
            parking.getParkingCells()[pairChosen.getI()][pairChosen.getJ()].setOccupied(true);
            int[][] matrixParking = getMatrixParking(pairChosen);
            WaveAlg path = new WaveAlg(matrixParking);
            path.findPath(indexIn.getJ() + 1, indexIn.getI() + 1,pairChosen.getJ()+1, pairChosen.getI()+1);
            ImageView imageView = (ImageView)anchorPane.getChildren().get(number);
            Platform.runLater(new UpdateAnchorPane(number, anchorPane));
            for(int i = 0; i < path.getWave().size(); i++){
                Platform.runLater(new UpdaterCarInParkingAdd(imageView, gridPane, path.getWave().get(i)));
                try {
                    sleep(1000);
                }
                catch (InterruptedException ex){
                    ex.printStackTrace();
                }
                if(i != path.getWave().size() - 1) {
                    Platform.runLater(new UpdaterCarParkingRemove(gridPane, imageView));
                }
            }
        }
    }

    @Override
    public void interrupt() {
        canWork = false;
    }

    public void setCanWork(boolean canWork){
        this.canWork = canWork;
    }

    public void setBooster(double booster) {
        this.booster = booster;
    }

    public synchronized void pauseThread(){
        isSuspended = true;
    }

    public synchronized void resumeThread(){
        isSuspended = false;
        notify();
    }

    public int[][] getMatrixParking(PairIJ pairChosen){
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
                if(parkingCell.getPattern().getPatternType() == PatternType.IN || parkingCell.getPattern().getPatternType() == PatternType.ROAD){
                    matrix[i+1][j+1] = -1;
                }
                else if(i == pairChosen.getI() && j == pairChosen.getJ()){
                    matrix[i+1][j+1] = -1;
                }
                else {
                    matrix[i+1][j+1] = 99;
                }
            }
        }
        return matrix;
    }

    /*public void goOn(){
        canWork = true;
        start();
    }*/
}
