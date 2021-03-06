package sample.threads;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import sample.controllers.ModellingController;
import sample.utils.LawDistribution;
import sample.models.Parking;
import sample.updaters.*;

import java.util.Calendar;
import java.util.Random;

public class FlowThread extends Thread {
    private AnchorPane anchorPane;
    private GridPane gridPane;
    private Label countNoFreePlaces;
    private Label clock;
    private volatile Parking parking;
    private LawDistribution flowLawDistribution;
    private LawDistribution stayLawDistribution;
    private double probabilityCar;
    private double probabilityIn;
    private volatile double booster;
    private volatile boolean canWork;
    private volatile boolean isSuspended;
    private volatile Calendar calendar;
    private int[] tariffs;
    private volatile int cashBox;

    public FlowThread(AnchorPane anchorPane, GridPane gridPane, Label countNoFreePlaces, Label clock){
        this.anchorPane = anchorPane;
        this.gridPane = gridPane;
        this.countNoFreePlaces = countNoFreePlaces;
        this.parking = ModellingController.getParking();
        this.flowLawDistribution = ModellingController.getFlowLawDistribution();
        this.stayLawDistribution = ModellingController.getStayLawDistribution();
        this.probabilityCar = ModellingController.getProbabilityCar();
        this.probabilityIn = ModellingController.getProbabilityIn();
        this.booster = ModellingController.getBooster().getBoost();
        isSuspended = false;
        canWork = true;
        this.calendar = ModellingController.getCalendar();
        this.clock = clock;
        this.cashBox = ModellingController.getCashBox();
        this.tariffs = ModellingController.getTariffs();
    }

    @Override
    public void run() {
        int number = 1;
        while (canWork){
            boolean isNormalCar;
            if(new Random().nextDouble() <= probabilityCar) {
                isNormalCar = true;
            }
            else{
                isNormalCar = false;
            }
            Platform.runLater(new UpdaterFlow(number, anchorPane,  isNormalCar));
            CarThread carThread = new CarThread(anchorPane, number, gridPane, countNoFreePlaces, clock,  isNormalCar);
            carThread.setName("CarThread" + number);
            carThread.start();
            try {
                int time = 0;
                switch (flowLawDistribution.getLawType()){
                    case UNIFORM:{
                        time = getTimeUniform(flowLawDistribution.getParameterOne(), flowLawDistribution.getParameterTwo());
                        break;
                    }
                    case NORMAL:{
                        time = getTimeNormal(flowLawDistribution.getParameterOne(), flowLawDistribution.getParameterTwo());
                        break;
                    }
                    case EXPONENTIAL:{
                        time = getTimeExponential(flowLawDistribution.getParameterOne());
                        break;
                    }
                    case DETERMINE:{
                        time = flowLawDistribution.getParameterOne();
                        break;
                    }
                }
                if (isSuspended) {
                    synchronized (this) {
                        while (isSuspended) {
                            wait();
                        }
                    }
                }
                sleep((long)(time * 1000/booster)); // интервал между появлением автомобиля в секундах
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
        return (int)(-1* Math.log(random.nextDouble())*lambda);
    }

    private int getTimeNormal(int mathExpectation, int variance){
        return (int) (Math.sqrt(variance)*getTimeNormalDefault() + mathExpectation);
    }

    private double getTimeNormalDefault(){
        Random random = new Random();
        return random.nextGaussian();
    }
}
