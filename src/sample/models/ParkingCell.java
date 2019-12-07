package sample.models;

import java.io.Serializable;

public class ParkingCell implements Serializable {
    private int coordinateHorizontal;
    private int coordinateVertical;
    private Pattern pattern;

    public ParkingCell(int coordinateHorizontal, int coordinateVertical, Pattern pattern) {
        this.coordinateHorizontal = coordinateHorizontal;
        this.coordinateVertical = coordinateVertical;
        this.pattern = pattern;
    }

    public int getCoordinateHorizontal() {
        return coordinateHorizontal;
    }

    public int getCoordinateVertical() {
        return coordinateVertical;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setCoordinateHorizontal(int coordinateHorizontal) {
        this.coordinateHorizontal = coordinateHorizontal;
    }

    public void setCoordinateVertical(int coordinateVertical) {
        this.coordinateVertical = coordinateVertical;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
