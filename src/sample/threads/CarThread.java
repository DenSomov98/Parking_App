package sample.threads;

import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import sample.PairIJ;
import sample.updaters.*;

import java.util.Random;

public class CarThread extends Thread{
    private AnchorPane anchorPane;
    private int number;
    private boolean canWork;
    private double booster;
    private volatile boolean isSuspended;
    private int coordinateXIn;
    private PairIJ indexIn;
    private GridPane gridPane;

    public CarThread(AnchorPane anchorPane, int number, double booster, int coordinateXIn, GridPane gridPane, PairIJ indexIn){
        this.anchorPane = anchorPane;
        this.number = number;
        this.booster = booster;
        canWork = true;
        isSuspended = false;
        this.coordinateXIn = coordinateXIn;
        this.gridPane = gridPane;
        this.indexIn = indexIn;
    }
    @Override
    public void run() {
        boolean isComeIn = false;
        double probabilityIn = 0.1;
        if(new Random().nextDouble() <= probabilityIn){
            isComeIn = true;
        }
        for (int i = 0; i < anchorPane.getWidth() + 50; i += 10) {
            if(canWork) {
                Platform.runLater(new UpdaterCar(i, anchorPane, number));
                if(isComeIn && i > coordinateXIn){
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

    /*public void goOn(){
        canWork = true;
        start();
    }*/
}
