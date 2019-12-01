package sample.threads;

import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import sample.updaters.*;

public class CarThread extends Thread{
    private AnchorPane anchorPane;
    private int number;
    private boolean canWork;
    private double booster;
    private volatile boolean isSuspended;

    public CarThread(AnchorPane anchorPane, int number, double booster){
        this.anchorPane = anchorPane;
        this.number = number;
        this.booster = booster;
        canWork = true;
        isSuspended = false;
    }
    @Override
    public void run() {
        for (int i = 0; i < anchorPane.getWidth() + 50; i += 10) {
            if(canWork) {
                Platform.runLater(new UpdaterCar(i, anchorPane, number));
                try {
                    sleep((long)(100/booster));
                    if(isSuspended){
                        synchronized (this){
                            while (isSuspended){
                                wait();
                            }
                        }
                    }
                } catch (InterruptedException ex) {
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
