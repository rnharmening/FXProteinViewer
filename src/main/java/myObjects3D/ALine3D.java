package myObjects3D;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

public class ALine3D extends Group {

    private final DoubleProperty startXProperty = new SimpleDoubleProperty(),
            startYProperty = new SimpleDoubleProperty(),
            startZProperty = new SimpleDoubleProperty(),
            endXProperty = new SimpleDoubleProperty(),
            endYProperty = new SimpleDoubleProperty(),
            endZProperty = new SimpleDoubleProperty();

    private final DoubleProperty height = new SimpleDoubleProperty();

    private Cylinder cylinderA;
    private Cylinder cylinderB;

    /**
     * Constructor for cylinder,
     * binds To properties and keeps cylinder aligned between the two points
     * @param startXProperty
     * @param startYProperty
     * @param startZProperty
     * @param endXProperty
     * @param endYProperty
     * @param endZProperty
     */
    public ALine3D(DoubleProperty radius,
                   DoubleProperty startXProperty, DoubleProperty startYProperty, DoubleProperty startZProperty,
                   DoubleProperty endXProperty, DoubleProperty endYProperty, DoubleProperty endZProperty) {

        this.startXProperty.bind(startXProperty);
        this.startYProperty.bind(startYProperty);
        this.startZProperty.bind(startZProperty);

        this.endXProperty.bind(endXProperty);
        this.endYProperty.bind(endYProperty);
        this.endZProperty.bind(endZProperty);

        this.cylinderA = setupCylinder(radius);
        this.cylinderB = setupCylinder(radius);

        this.height.addListener(c -> {
            recalculateCylinder();
        });

        bindHeightToStartEndDistance();

        this.getChildren().addAll(this.cylinderA, this.cylinderB);

    }

    private Cylinder setupCylinder(DoubleProperty radius) {
        Cylinder cylinder = new Cylinder();
        cylinder.radiusProperty().bind(radius);
        cylinder.heightProperty().bind(this.height.divide(2));
        return cylinder;
    }

    private void recalculateCylinder(){
        Point3D startV = new Point3D(startXProperty.get(), startYProperty.get(), startZProperty.get());
        Point3D endV   = new Point3D(endXProperty.get(),   endYProperty.get(),   endZProperty.get());
        Point3D midV   = startV.midpoint(endV);
        Point3D[] anchorPoints = new Point3D[] {midV.midpoint(startV), midV.midpoint(endV)};
        Cylinder[] cylinders = new Cylinder[] {cylinderA, cylinderB};

        Point3D directionV = endV.subtract(startV);
        Point3D perpendicularAxisV  = AxisPoints.Y_AXIS.crossProduct(directionV);
        Double angle= directionV.angle(AxisPoints.Y_AXIS);

        for (int i=0; i< cylinders.length; i++){
            cylinders[i].setRotationAxis(perpendicularAxisV);
            cylinders[i].setRotate(angle);

            cylinders[i].setTranslateX(anchorPoints[i].getX());
            cylinders[i].setTranslateY(anchorPoints[i].getY());
            cylinders[i].setTranslateZ(anchorPoints[i].getZ());
        }

    }

    private void bindHeightToStartEndDistance(){
        this.height.bind(Bindings.createDoubleBinding(
                () -> Math.sqrt(Math.pow(endXProperty.get() - startXProperty.get(), 2) +
                                Math.pow(endYProperty.get() - startYProperty.get(), 2) +
                                Math.pow(endZProperty.get() - startZProperty.get(), 2)),
                endXProperty, startXProperty, endYProperty, startYProperty, endZProperty, startZProperty
        ));
    }

    public void changeMaterial(Material materialA, Material materialB){
        this.cylinderA.setMaterial(materialA);
        this.cylinderB.setMaterial(materialB);
    }
}
