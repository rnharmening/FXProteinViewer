package graphView3D;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import util.ColorUtil;

public class ANodeView3D extends AObjectView3D {

    public static final double DEF_RADIUS = 0.5;
    public static final int DIVISIONS = 64;

    private final IntegerProperty nodeId = new SimpleIntegerProperty();
    private Sphere sphere;

    public ANodeView3D() {
        this(DEF_RADIUS, 0,0,0, ColorUtil.randomMaterial());
    }

    public ANodeView3D(double radius, double x, double y, double z){
        this(radius, x, y, z, ColorUtil.randomMaterial());
    }

    public ANodeView3D(double radius, double x, double y, double z, DrawModeUtil drawModeUtil){
        this.sphere = new Sphere(radius, DIVISIONS);

        // This binding is quite long, but has great functionality:
        //     double              0 or 1        double          1 or 0              double
        // scaleMultiplier * ((includeAtomSize * radius) + ((-includeAtomSize+1) * defaultRadius))
        this.sphere.radiusProperty().bind(
                drawModeUtil.radiusMultiplierPropertyProperty()
                .multiply(drawModeUtil.includeAtomSizePropertyProperty().multiply(radius)
                        .add(
                                (drawModeUtil.includeAtomSizePropertyProperty().negate().add(1))
                                        .multiply(1))));


        this.getChildren().addAll(this.sphere);

        this.setTranslateX(x);
        this.setTranslateY(y);
        this.setTranslateZ(z);
    }


    public ANodeView3D(double radius, double x, double y, double z, PhongMaterial phongMaterial) {
        this.sphere = new Sphere(radius, DIVISIONS);
        this.sphere.setMaterial(phongMaterial);

        this.getChildren().add(this.sphere);

        this.setTranslateX(x);
        this.setTranslateY(y);
        this.setTranslateZ(z);
    }

    public int getNodeId() {
        return nodeId.get();
    }

    public IntegerProperty nodeIdProperty() {
        return nodeId;
    }

    public void setCoordinates(double x, double y, double z) {
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.setTranslateZ(z);
    }

    public void setDrawMode(DrawMode drawMode) {
        this.sphere.setDrawMode(drawMode);
    }

    public DrawMode getDrawMode(){
        return this.sphere.getDrawMode();
    }

    public void setColor(Color color) {
        this.sphere.setMaterial(new PhongMaterial(color));
    }

    public Material getMaterial() {
        return this.sphere.getMaterial();
    }

    public void setIsSelected(boolean selected){
    }
}
