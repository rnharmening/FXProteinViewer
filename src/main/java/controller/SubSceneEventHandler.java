package controller;

import graphView3D.ANodeView3D;
import javafx.beans.property.Property;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import myObjects3D.AxisPoints;
import graphView3D.AGraphView3D;
import proteinModel.Atom;

public class SubSceneEventHandler {

    private final double rotateFactor = 0.35;
    private Point3D pivotPoint = new Point3D(0,0,0);
    private double orgSceneX, orgSceneY;
    private Rotate rotate = new Rotate();
    private AGraphView3D graphView3D;

    private Property<Transform> viewTransformProperty;

    protected SubSceneEventHandler(AGraphView3D graphView3D, Property<Transform> viewTransformProperty) {
        this.graphView3D = graphView3D;
        this.viewTransformProperty = viewTransformProperty;
    }

    // adds listener to update group orientation on viewTransform change
    protected void setUpViewTransformListener() {
        viewTransformProperty.addListener((c, o, n) ->
                this.graphView3D.getTransforms().setAll(viewTransformProperty.getValue()));
    }


    protected void setupSubSceneEvents(Node parent, Camera camera){
        parent.setOnScroll(event -> {
            double delta = 1.1;

            double scale = camera.getTranslateZ(); // currently we only use Y, same value is used for X

            if (event.getDeltaY() < 0)
                scale /= delta;
            else if(event.getDeltaY() > 0)
                scale *= delta;

            camera.setTranslateZ(scale);
        });

        parent.setOnMousePressed(onMousePressedEventHandler);
        parent.setOnMouseDragged(onMouseDraggedEventHandler);
    }

    protected EventHandler<MouseEvent> onMousePressedEventHandler =
            new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent t) {
                    orgSceneX = t.getSceneX();
                    orgSceneY = t.getSceneY();
                    pivotPoint = graphView3D.calculateCenterPoint3D();
                }
            };

    protected EventHandler<MouseEvent> onMouseDraggedEventHandler =
            new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent t) {
                    double offsetX = t.getSceneX() - orgSceneX;
                    double offsetY = t.getSceneY() - orgSceneY;
                    orgSceneX = t.getSceneX();
                    orgSceneY = t.getSceneY();


                    Point3D dragV = new Point3D(offsetX, offsetY, 0);
                    Point3D axis = dragV.crossProduct(AxisPoints.Z_AXIS);
                    double angle = rotateFactor * dragV.magnitude();
                    rotate.setPivotX(pivotPoint.getX());
                    rotate.setPivotY(pivotPoint.getY());
                    rotate.setPivotZ(pivotPoint.getZ());
                    rotate.setAxis(axis);
                    rotate.setAngle(angle);
                    viewTransformProperty.setValue(
                            rotate.createConcatenation(viewTransformProperty.getValue()));
                }
            };
}
