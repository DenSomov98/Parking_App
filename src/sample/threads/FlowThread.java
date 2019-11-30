package sample.threads;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import sample.updaters.*;

import java.util.Random;

public class FlowThread extends Thread {
    private AnchorPane anchorPane;
    private double booster;
    private boolean canWork;
    private volatile boolean isSuspended;

    public FlowThread(AnchorPane anchorPane, double booster){
        this.anchorPane = anchorPane;
        this.booster = booster;
        isSuspended = false;
        canWork = true;
    }

    @Override
    public void run() {
        int number = 1;
        while (canWork){
            Platform.runLater(new UpdaterFlow(number, anchorPane));
            CarThread carThread = new CarThread(anchorPane, number);
            carThread.setName("CarThread" + number);
            carThread.start();
            try {
                int time = getTimeUniform();
                sleep(time * 1000);
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

    private int getTimeUniform(){
        int a = 1;
        int b = 5;
        Random random = new Random();
        return a + random.nextInt(b-a+1);
    }
}
