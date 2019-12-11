package sample.updaters;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class UpdateAnchorPane implements Runnable{
    private AnchorPane anchorPane;
    private int number;

    public UpdateAnchorPane(int number, AnchorPane anchorPane) {
        this.number = number;
        this.anchorPane = anchorPane;
    }

    @Override
    public void run() {
        anchorPane.getChildren().set(number, new ImageView());
    }
}
