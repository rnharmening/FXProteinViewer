package graphView3D;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import myObjects3D.ALine3D;

public class AEdgeView3D extends AObjectView3D {

    private final IntegerProperty edgeID = new SimpleIntegerProperty();
    private ALine3D line3D;

    AEdgeView3D(ANodeView3D startNode, ANodeView3D endNode, DrawModeUtil drawModeUtil) {

        DoubleProperty sx = startNode.translateXProperty();
        DoubleProperty sy = startNode.translateYProperty();
        DoubleProperty sz = startNode.translateZProperty();

        DoubleProperty ex = endNode.translateXProperty();
        DoubleProperty ey = endNode.translateYProperty();
        DoubleProperty ez = endNode.translateZProperty();

        DoubleProperty edgeRadius = new SimpleDoubleProperty();

        edgeRadius.bind(drawModeUtil.bondRadiusMultiplierPropertyProperty().multiply(drawModeUtil.radiusMultiplierPropertyProperty()));

        this.line3D = new ALine3D(edgeRadius, sx, sy, sz, ex, ey, ez);
        this.line3D.changeMaterial(startNode.getMaterial(), endNode.getMaterial());

        this.getChildren().add(this.line3D);

    }

    public int getEdgeId() {
        return edgeID.get();
    }

    public IntegerProperty edgeIdProperty() {
        return edgeID;
    }

    public void changeColor(Color colorA, Color colorB){
        this.line3D.changeMaterial(new PhongMaterial(colorA), new PhongMaterial(colorB));
    }

}
