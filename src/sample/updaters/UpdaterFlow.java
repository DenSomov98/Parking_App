package sample.updaters;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import sample.controllers.ModellingController;

import java.util.Random;

public class UpdaterFlow implements Runnable{
    private int number;
    private AnchorPane anchorPane;
    private volatile boolean isNormalCar;

    public UpdaterFlow(int number, AnchorPane anchorPane, boolean isNormalCar) {
        this.number = number;
        this.anchorPane = anchorPane;
        this.isNormalCar = isNormalCar;
    }

    @Override
    public void run() {
        int max = Math.max(ModellingController.getParking().getHorizontalSize(), ModellingController.getParking().getVerticalSize());
        ImageView imageView;
        if(isNormalCar) {
            if(new Random().nextDouble() <= 0.5) {
                imageView = new ImageView(new Image(getClass().getResourceAsStream("../images/carModelling-min.JPG")));
                isNormalCar = true;
                imageView.setFitWidth((double)303/(max+2));
               // imageView.setFitWidth(45);
                //imageView.setFitWidth(30);
            }
            else {
                imageView = new ImageView(new Image(getClass().getResourceAsStream("../images/carModellingRed-min.JPG")));
                isNormalCar = true;
                imageView.setFitWidth((double)303/(max+2));
                //imageView.setFitWidth(35);
            }
        }
        else {
            imageView = new ImageView(new Image(getClass().getResourceAsStream("../images/truckModelling.JPG")));
            isNormalCar = false;
            imageView.setFitWidth((double)303/(max+2) + 10);
            //imageView.setFitWidth(55);
            //imageView.setFitWidth(25);
        }
        imageView.setFitHeight((double)316/(max+3));
        //imageView.setFitHeight(35);
        //imageView.setFitHeight(30);
        anchorPane.getChildren().add(number, imageView);
        anchorPane.getChildren().get(number).setLayoutX(0);
        anchorPane.getChildren().get(number).setLayoutY(0);
    }
}
