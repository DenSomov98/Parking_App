package sample.enums;

public enum Booster {
    //блок замедлителей
    INHIBITOR_X100(-6, 0.01),
    INHIBITOR_X50(-5, 0.02),
    INHIBITOR_X20(-4, 0.05),
    INHIBITOR_X10(-3, 0.1),
    INHIBITOR_X5(-2, 0.2),
    INHIBITOR_X2(-1, 0.5),
    //отсутствие ускорения(режим реального времени)
    BOOSTER_DEFAULT(0, 1),
    //блок ускорителей
    BOOSTER_X2(1, 2),
    BOOSTER_X5(2, 5),
    BOOSTER_X10(3, 10),
    BOOSTER_X20(4, 20),
    BOOSTER_X50(5, 50),
    BOOSTER_X100(6, 100);

    private int code;
    private double boost;

    Booster(int code, double boost){
        this.code = code;
        this.boost = boost;
    }

    public int getCode() {
        return code;
    }

    public double getBoost() {
        return boost;
    }

    public static Booster getBooster(int code){
        Booster[] boosters = Booster.values();
        for(Booster booster: boosters){
            if(booster.code == code){
                return booster;
            }
        }
        return null;
    }
}
