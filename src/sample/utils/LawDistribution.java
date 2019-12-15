package sample.utils;

import sample.enums.LawType;

public class LawDistribution {
    public static final int NO_PARAMETER = -1;
    private LawType lawType;
    private int parameterOne;
    private int parameterTwo;

    public LawDistribution(LawType lawType, int parameterOne, int parameterTwo) {
        this.lawType = lawType;
        this.parameterOne = parameterOne;
        this.parameterTwo = parameterTwo;
    }

    public LawType getLawType() {
        return lawType;
    }

    public void setLawType(LawType lawType) {
        this.lawType = lawType;
    }

    public int getParameterOne() {
        return parameterOne;
    }

    public void setParameterOne(int parameterOne) {
        this.parameterOne = parameterOne;
    }

    public int getParameterTwo() {
        return parameterTwo;
    }

    public void setParameterTwo(int parameterTwo) {
        this.parameterTwo = parameterTwo;
    }
}
