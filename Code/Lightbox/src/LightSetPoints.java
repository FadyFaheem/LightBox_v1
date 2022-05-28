public class LightSetPoints {
    int setPoint;

    int milVolt;

    public LightSetPoints(int setPoint, int milVolt) {
        this.milVolt = milVolt;
        this.setPoint = setPoint;
    }
    public int getSetPoint() {
        return setPoint;
    }

    public void setSetPoint(int setPoint) {
        this.setPoint = setPoint;
    }

    public int getMilVolt() {
        return milVolt;
    }

    public void setMilVolt(int milVolt) {
        this.milVolt = milVolt;
    }

}
