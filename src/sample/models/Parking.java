package sample.models;

import sample.enums.PatternType;

import java.io.Serializable;

public class Parking implements Serializable {
    private int horizontalSize;
    private int verticalSize;
    private ParkingCell[][] parkingCells;

    public Parking(int horizontalSize, int verticalSize) {
        this.horizontalSize = horizontalSize;
        this.verticalSize = verticalSize;
        parkingCells = new ParkingCell[horizontalSize][verticalSize];
        for(int i = 0; i < horizontalSize; i++){
            for(int j = 0; j < verticalSize; j++){
                parkingCells[i][j] = new ParkingCell(i, j, new Pattern(PatternType.EMPTY));
            }
        }
    }

    public int getHorizontalSize() {
        return horizontalSize;
    }

    public void setHorizontalSize(int horizontalSize) {
        this.horizontalSize = horizontalSize;
    }

    public int getVerticalSize() {
        return verticalSize;
    }

    public void setVerticalSize(int verticalSize) {
        this.verticalSize = verticalSize;
    }

    public ParkingCell[][] getParkingCells() {
        return parkingCells;
    }

    public void setParkingCells(ParkingCell[][] parkingCells) {
        this.parkingCells = parkingCells;
    }

    public void addPattern(int positionI, int positionJ, Pattern pattern){
        parkingCells[positionI][positionJ].setPattern(pattern);
    }

    public void removePattern(int positionI, int positionJ){
        parkingCells[positionI][positionJ].setPattern(new Pattern(PatternType.EMPTY));
    }

    public void movePattern(int positionIOld, int positionJOld, int positionINew, int positionJNew){
        Pattern pattern = parkingCells[positionIOld][positionJOld].getPattern();
        parkingCells[positionIOld][positionJOld].setPattern(new Pattern(PatternType.EMPTY));
        parkingCells[positionINew][positionJNew].setPattern(pattern);
    }
}
