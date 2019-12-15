package sample.threads;

import javafx.application.Platform;
import javafx.scene.control.Label;
import java.util.Calendar;
import sample.updaters.*;

public class TimeThread extends Thread{
    private volatile Calendar calendar;
    private volatile Label clock;
    private volatile boolean canWork;
    private volatile boolean isSuspended;
    private volatile double booster;

    public TimeThread(Calendar calendar, Label clock, double booster){
        this.calendar = calendar;
        this.clock = clock;
        this.booster = booster;
        isSuspended = false;
        canWork = true;
    }
    @Override
    public void run() {
        while (canWork) {
            calendar.add(Calendar.SECOND, 1);
            Platform.runLater(new UpdaterTime(calendar, clock));
            try {
                sleep((long) (1000 / booster));
                if(isSuspended){
                    synchronized (this){
                        while (isSuspended){
                            wait();
                        }
                    }
                }
            } catch (InterruptedException ignored) {

            }
        }
    }
    @Override
    public void interrupt() {
        canWork = false;
        clock.setText("00:00:00");
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
}
