package sample.updaters;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;


public class UpdaterCarParkingRemove implements Runnable {
    private GridPane gridPane;
    private ImageView imageView;

    public UpdaterCarParkingRemove(GridPane gridPane, ImageView imageView) {
        this.gridPane = gridPane;
        this.imageView = imageView;
    }

    @Override
    public void run() {
        gridPane.getChildren().remove(imageView);
    }

    private int coordinatesToIndex(int i, int j){
        return (i * 5 + j + 1);
    }
}
