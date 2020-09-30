package graphView3D;

import javafx.beans.property.*;

public class DrawModeUtil {

    private DoubleProperty radiusMultiplierProperty = new SimpleDoubleProperty();
    private IntegerProperty includeAtomSizeProperty = new SimpleIntegerProperty();

    private DoubleProperty bondRadiusMultiplierProperty = new SimpleDoubleProperty();

    /**
     *
     */
    public DrawModeUtil() {
        reset();
    }

    public double getRadiusMultiplierProperty() {
        return radiusMultiplierProperty.get();
    }

    public DoubleProperty radiusMultiplierPropertyProperty() {
        return radiusMultiplierProperty;
    }

    public void setRadiusMultiplierProperty(double radiusMultiplierProperty) {
        this.radiusMultiplierProperty.set(radiusMultiplierProperty);
    }

    public boolean isIncludeAtomSizeProperty() {
        return includeAtomSizeProperty.get() > 0 ? true : false;
    }

    public IntegerProperty includeAtomSizePropertyProperty() {
        return includeAtomSizeProperty;
    }

    public void setIncludeAtomSizeProperty(boolean includeAtomSize) {
        this.includeAtomSizeProperty.set(includeAtomSize ? 1 : 0);
    }

    public double getBondRadiusMultiplierProperty() {
        return bondRadiusMultiplierProperty.get();
    }

    public DoubleProperty bondRadiusMultiplierPropertyProperty() {
        return bondRadiusMultiplierProperty;
    }

    public void setBondRadiusMultiplierProperty(double bondRadiusMultiplierProperty) {
        this.bondRadiusMultiplierProperty.set(bondRadiusMultiplierProperty);
    }

    public void reset(){
        radiusMultiplierProperty.setValue(1);
        includeAtomSizeProperty.setValue(1);

        bondRadiusMultiplierProperty.setValue(0.2);
    }
}
