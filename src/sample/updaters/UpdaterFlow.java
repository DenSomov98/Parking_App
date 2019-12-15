package sample.updaters;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.Random;

public class UpdaterFlow implements Runnable{
    private int number;
    private AnchorPane anchorPane;
    private double probabilityCar;

    public UpdaterFlow(int number, AnchorPane anchorPane, double probabilityCar) {
        this.number = number;
        this.anchorPane = anchorPane;
        this.probabilityCar = probabilityCar;
    }

    @Override
    public void run() {
        ImageView imageView;
        if(new Random().nextDouble() <= probabilityCar) {
            if(new Random().nextDouble() <= 0.5) {
                imageView = new ImageView(new Image(getClass().getResourceAsStream("../images/carModelling-min.JPG")));
                //imageView = new ImageView(new Image(getClass().getResourceAsStream("../images/stop.JPG")));
                imageView.setFitWidth(45);
                //imageView.setFitWidth(30);
            }
            else {
                imageView = new ImageView(new Image(getClass().getResourceAsStream("../images/carModellingRed-min.JPG")));
                imageView.setFitWidth(55);
                //imageView.setFitWidth(35);
            }
        }
        else {
            imageView = new ImageView(new Image(getClass().getResourceAsStream("../images/truckModelling.JPG")));
            imageView.setFitWidth(55);
            //imageView.setFitWidth(25);
        }
        imageView.setFitHeight(35);
        //imageView.setFitHeight(30);
        anchorPane.getChildren().add(number, (Node)imageView);
        anchorPane.getChildren().get(number).setLayoutX(0);
        anchorPane.getChildren().get(number).setLayoutY(0);
    }
}
