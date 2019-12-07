package sample.models;

import sample.enums.PatternType;
import sample.enums.Rotation;

import java.io.Serializable;

public class Pattern implements Serializable {

    private PatternType patternType;
    private Rotation rotation;
    private int id;

    public Pattern(PatternType patternType, Rotation rotation, int id){
        this.patternType = patternType;
        this.rotation = rotation;
        this.id = id;
    }

    public Pattern(PatternType patternType){
        this.patternType = patternType;
        this.rotation = Rotation.NONE;
        this.id = 0;
    }

    public PatternType getPatternType() {
        return patternType;
    }

    public void setPatternType(PatternType patternType) {
        this.patternType = patternType;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /*CAR("../images/car.JPG"),//, Rotation.NONE, 0),
    IN("../images/arrowin.JPG"),// Rotation.SOUTH, 0),
    OUT("../images/arrowout.JPG"),// Rotation.NORTH, 0),
    CASH("../images/cash.JPG"),// Rotation.NONE, 0),
    ROAD("../images/road.JPG"),// Rotation.NONE, 0),
    TRUCK("../images/bigcar.JPG"),// Rotation.NONE, 0),
    TRUCK_HEAD("../images/truckhead.JPG"),// Rotation.EAST, 0),
    TRUCK_TAIL("../images/trucktail.JPG"),//  Rotation.EAST, 0),
    TRUCK_MERGED("../images/truck_merged.JPG"),// Rotation.EAST, 0),
    EMPTY("../images/empty.JPG");//, Rotation.NONE, 0);*/

    /*public Pattern(String path){ //,Rotation rotation, int id) {
        this.path = path;
        this.rotation = Rotation.NONE;
        this.id = 0;
        //this.rotation = rotation;
       // this.id = id;
    }*/

    /*public String getPath() {
        return path;
    }*/

    /*public void setPath(String path) {
        this.path = path;
    }*/

    /*public Rotation getRotation() {
        return rotation;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }*/
}
