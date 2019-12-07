package sample.enums;

import java.io.Serializable;

public enum PatternType implements Serializable {
    CAR("../images/car.JPG"),
    IN("../images/arrowin.JPG"),
    OUT("../images/arrowout.JPG"),
    CASH("../images/cash.JPG"),
    ROAD("../images/road.JPG"),
    TRUCK("../images/bigcar.JPG"),
    TRUCK_HEAD("../images/truckhead.JPG"),
    TRUCK_TAIL("../images/trucktail.JPG"),
    TRUCK_MERGED("../images/truck_merged.JPG"),
    EMPTY("../images/empty.JPG");

    private String path;

    PatternType(String path){
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
