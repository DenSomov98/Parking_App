package sample.updaters;

import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class UpdaterCar implements Runnable {
    private int number;
    private AnchorPane anchorPane;
    private int i;

    public UpdaterCar(int i, AnchorPane anchorPane, int number) {
        this.number = number;
        this.i = i;
        this.anchorPane = anchorPane;
    }

    @Override
    public void run() {
        /*if(i < coordinateXIn) {
            anchorPane.getChildren().get(number).setLayoutX(i);
        }
        else{
            InnerShadow innerShadow = new InnerShadow();
            innerShadow.setColor(Color.BLACK);
            anchorPane.getChildren().get(number).setEffect(innerShadow);
        }*/
        anchorPane.getChildren().get(number).setLayoutX(i);
        /*if(i < coordinateXIn || !isComeIn) {
            anchorPane.getChildren().get(number).setLayoutX(i);
        }
        else if(isComeIn&& i > coordinateXIn){
            anchorPane.getChildren().get(number).setLayoutX(coordinateXIn);
            anchorPane.getChildren().get(number).setLayoutY(75);
        }*/
    }
}
