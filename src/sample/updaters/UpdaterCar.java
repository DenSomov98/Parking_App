package sample.updaters;

import javafx.scene.layout.AnchorPane;

public class UpdaterCar implements Runnable {
    private int number;
    private AnchorPane anchorPane;
    private int i;

    public UpdaterCar(int i, AnchorPane anchorPane, int number){
        this.number = number;
        this.i = i;
        this.anchorPane = anchorPane;
    }
    @Override
    public void run() {
        anchorPane.getChildren().get(number).setLayoutX(i);
    }
}
