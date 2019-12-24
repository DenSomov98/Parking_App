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

}
