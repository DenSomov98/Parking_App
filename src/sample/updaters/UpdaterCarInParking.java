package sample.updaters;

import javafx.scene.effect.ColorInput;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import sample.PairIJ;

import java.awt.*;


public class UpdaterCarInParking implements Runnable {
    private int number;
    private GridPane gridPane;
    private AnchorPane anchorPane;
    private PairIJ indexIn;

    public UpdaterCarInParking(int number, GridPane gridPane, AnchorPane anchorPane, PairIJ indexIn) {
        this.number = number;
        this.gridPane = gridPane;
        this.anchorPane = anchorPane;
        this.indexIn = indexIn;
    }

    @Override
    public void run() {
        ImageView imageView = (ImageView) anchorPane.getChildren().get(number);
        anchorPane.getChildren().set(number, new ImageView());
        /*ImageView imageViewNew = new ImageView(imageView.getImage());
        imageViewNew.setFitHeight(imageView.getFitHeight());
        imageViewNew.setFitWidth(imageView.getFitWidth());
        imageViewNew.setEffect(getWhiteColorEffect(imageView.getFitHeight(), imageView.getFitWidth()));
        anchorPane.getChildren().set(number, imageViewNew);
        anchorPane.getChildren().get(number).setLayoutX(imageView.getLayoutX());*/
        gridPane.add(imageView, indexIn.getJ(), indexIn.getI());
    }

    private InnerShadow getShadowEffect(Color color){
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(color);
        return innerShadow;
    }

    private ColorInput getWhiteColorEffect(double height, double width){
        ColorInput colorInput = new ColorInput();
        colorInput.setHeight(height);
        colorInput.setWidth(width);
        colorInput.setPaint(Color.WHITE);
        return colorInput;
    }
}
