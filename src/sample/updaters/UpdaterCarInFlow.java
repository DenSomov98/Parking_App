package sample.updaters;

import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class UpdaterCarInFlow implements Runnable {
    private int number;
    private AnchorPane anchorPane;
    private int i;

    public UpdaterCarInFlow(int i, AnchorPane anchorPane, int number) {
        this.number = number;
        this.i = i;
        this.anchorPane = anchorPane;
    }

    @Override
    public void run() {
        anchorPane.getChildren().get(number).setLayoutX(i);
    }
}
