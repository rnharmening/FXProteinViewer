package graphView3D;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.Property;
import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import proteinModel.ASelectionModel;
import proteinModel.Atom;
import graphView3D.AGraphView3D;
import graphView3D.ANodeView3D;
import proteinModel.Residue;

import java.util.HashMap;
import java.util.Map;

/**
 * maintains 2D bounding boxes for 3D node views
 * Daniel Huson, 11.2017
 */
public class BoundingBoxes2D extends Group{

    private final Map<ANodeView3D, Rectangle> node2rectangle = new HashMap<>();
    private final Map<ARibbonView3D, Rectangle> ribbon2rectangle = new HashMap<>();

    /**
     * constructor
     *
     * @param properties
     */
    public BoundingBoxes2D(Pane bottomPane, AGraphView3D graphView, ASelectionModel<Residue> selectionModel, Property... properties) {
        selectionModel.getSelectedItems().addListener((ListChangeListener<Residue>) c -> {
            while (c.next()) {
                for (Residue residue : c.getRemoved()) {
                    for (Atom atom : residue.getUnmodifiableAtoms()){
                        try {
                            ANodeView3D nodeView = graphView.getViewNode(atom.getSerial());
                            this.getChildren().remove(node2rectangle.get(nodeView));
                            node2rectangle.remove(nodeView);
                        } catch (NullPointerException e) {
                            System.err.println("Bounding Box Remove not working");
                            System.err.println(e.getMessage());
                        }
                    }
                    ARibbonView3D ribbon = graphView.getRibbon(residue.getResidueId());
                    this.getChildren().remove(ribbon2rectangle.get(ribbon));
                    ribbon2rectangle.remove(ribbon);


                }
                for (Residue residue : c.getAddedSubList()) {
                    for (Atom atom : residue.getUnmodifiableAtoms()){
                        ANodeView3D nodeView = graphView.getViewNode(atom.getSerial());
                        nodeView.setIsSelected(true);
                        final Rectangle rect = createBoundingBoxWithBinding(bottomPane, nodeView, properties);
                        node2rectangle.put(nodeView, rect);
                        this.getChildren().add(rect);
                    }

                    ARibbonView3D ribbon = graphView.getRibbon(residue.getResidueId());
                    if (ribbon != null) {
                        final Rectangle rect = createBoundingBoxWithBinding(bottomPane, ribbon, properties);
                        ribbon2rectangle.put(ribbon, rect);
                        this.getChildren().add(rect);
                    }
                }
            }
        });
    }

    /**
     * create a bounding box that is bound to user determined transformations
     */
    private static Rectangle createBoundingBoxWithBinding(Pane pane, Node node, final Property... properties) {
        final Rectangle boundingBox = new Rectangle();
        boundingBox.setStroke(Color.GOLDENROD);
        boundingBox.setStrokeWidth(2);
        boundingBox.setFill(Color.TRANSPARENT);
        boundingBox.setMouseTransparent(true);

        boundingBox.visibleProperty().bind(node.visibleProperty());

        final ObjectBinding<Rectangle> binding = new ObjectBinding<Rectangle>() {
            {
                bind(properties);
                bind(node.translateXProperty());
                bind(node.translateYProperty());
                bind(node.translateZProperty());
                bind(node.scaleXProperty());
            }

            @Override
            protected Rectangle computeValue() {
                return computeRectangle(pane, node);
            }
        };

        binding.addListener((c, o, n) -> {
            boundingBox.setX(n.getX());
            boundingBox.setY(n.getY());
            boundingBox.setWidth(n.getWidth());
            boundingBox.setHeight(n.getHeight());
        });
        boundingBox.setUserData(binding);

        binding.invalidate();
        return boundingBox;
    }

    private static Rectangle computeRectangle(Pane pane, Node node) {
        try {
            final Bounds boundsOnScreen = node.localToScreen(node.getBoundsInLocal());
            final Bounds paneBoundsOnScreen = pane.localToScreen(pane.getBoundsInLocal());
            final double xInScene = boundsOnScreen.getMinX() - paneBoundsOnScreen.getMinX();
            final double yInScene = boundsOnScreen.getMinY() - paneBoundsOnScreen.getMinY();
            return new Rectangle(xInScene, yInScene, boundsOnScreen.getWidth(), boundsOnScreen.getHeight());
        } catch (NullPointerException e) {
            return new Rectangle(0, 0, 0, 0);
        }
    }
}
