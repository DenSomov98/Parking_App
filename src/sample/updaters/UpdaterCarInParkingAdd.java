package sample.updaters;

import javafx.scene.Node;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import sample.PairIJ;

import java.awt.*;
import java.util.ArrayList;


public class UpdaterCarInParkingAdd implements Runnable {
    private GridPane gridPane;
    private PairIJ pairIJ;
    private Node node;

    public UpdaterCarInParkingAdd(Node node, GridPane gridPane, PairIJ pairIJ) {
        this.node = node;
        this.gridPane = gridPane;
        this.pairIJ = pairIJ;
    }

    @Override
    public void run() {
        gridPane.add(node, pairIJ.getI()-1, pairIJ.getJ()-1);
    }

}
