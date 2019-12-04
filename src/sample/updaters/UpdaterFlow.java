package sample.updaters;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.Random;

public class UpdaterFlow implements Runnable{
    private int number;
    private AnchorPane anchorPane;

    public UpdaterFlow(int number, AnchorPane anchorPane) {
        this.number = number;
        this.anchorPane = anchorPane;
    }

    @Override
    public void run() {
        double probabilityCar = 0.5;
        ImageView imageView;
        if(new Random().nextDouble() <= probabilityCar) {
            imageView = new ImageView(new Image(getClass().getResourceAsStream("../images/carModelling.JPG")));
            imageView.setFitWidth(45);
        }
        else {
            imageView = new ImageView(new Image(getClass().getResourceAsStream("../images/truckModelling.JPG")));
            imageView.setFitWidth(55);
        }
        imageView.setFitHeight(35);
        anchorPane.getChildren().add(number, (Node)imageView);
        anchorPane.getChildren().get(number).setLayoutX(0);
        anchorPane.getChildren().get(number).setLayoutY(0);
    }
}
