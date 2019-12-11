package sample.threads;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import sample.PairIJ;
import sample.enums.LawType;
import sample.models.Parking;
import sample.updaters.*;

import java.util.Random;

public class FlowThread extends Thread {
    private AnchorPane anchorPane;
    private GridPane gridPane;
    private double booster;
    private boolean canWork;
    private volatile boolean isSuspended;
    private int paramFirst;
    private int paramSecond;
    private LawType lawType;
    private int coordinateXIn;
    private PairIJ indexIn;
    private Parking parking;

    public FlowThread(AnchorPane anchorPane, double booster, int paramFirst, int paramSecond, LawType lawType, int coordinateXIn, GridPane gridPane, PairIJ indexIn, Parking parking){
        this.anchorPane = anchorPane;
        this.booster = booster;
        isSuspended = false;
        canWork = true;
        this.paramFirst = paramFirst;
        this.paramSecond = paramSecond;
        this.lawType = lawType;
        this.coordinateXIn = coordinateXIn;
        this.gridPane = gridPane;
        this.indexIn = indexIn;
        this.parking = parking;
    }

    @Override
    public void run() {
        int number = 1;
        while (canWork){
            Platform.runLater(new UpdaterFlow(number, anchorPane));
            CarThread carThread = new CarThread(anchorPane, number, booster, coordinateXIn, gridPane, indexIn, parking);
            carThread.setName("CarThread" + number);
            carThread.start();
            try {
                int time = 0;
                switch (lawType){
                    case UNIFORM:{
                        time = getTimeUniform(paramFirst, paramSecond);
                        break;
                    }
                    case NORMAL:{
                        time = getTimeNormal(paramFirst, paramSecond);
                        break;
                    }
                    case EXPONENTIAL:{
                        time = getTimeExponential(paramFirst);
                        break;
                    }
                    case DETERMINE:{
                        time = paramFirst;
                        break;
                    }
                }
                sleep((long)(time * 1000/booster));
                if(isSuspended){
                    synchronized (this){
                        while (isSuspended){
                           wait();
                        }
                    }
                }
            }
            catch (InterruptedException ignored){}
            number++;
        }
    }

    @Override
    public void interrupt() {
        canWork = false;
        for(Node node: anchorPane.getChildren()){
            if(node instanceof ImageView) {
                node.setVisible(false);
            }
        }
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
