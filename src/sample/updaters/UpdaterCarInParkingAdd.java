package sample.updaters;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import sample.utils.PairIJ;


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
        gridPane.add(node, pairIJ.getI() - 1, pairIJ.getJ() - 1);
    }

}
