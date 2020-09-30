package graphView3D;

// import graph.ANode;
import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.HashMap;

public class AGraphView3D extends Group {

    DrawModeUtil drawModeUtil = new DrawModeUtil();

    private final Group nodeViewGroup = new Group(),
                        edgeViewGroup = new Group(),
                        ribbonViewGroup = new Group();

    private final ObservableList<ANodeView3D> nodeList = FXCollections.observableArrayList();
    private final ObservableList<AEdgeView3D> edgeList = FXCollections.observableArrayList();
    private final ObservableList<ARibbonView3D> ribbonList = FXCollections.observableArrayList();

    private ObservableList<ANodeView3D> unmodifiableNodeList = FXCollections.unmodifiableObservableList(nodeList);
    private ObservableList<AEdgeView3D> unmodifiableEdgeList = FXCollections.unmodifiableObservableList(edgeList);
    private ObservableList<ARibbonView3D> unmodifiableRibbonList = FXCollections.unmodifiableObservableList(ribbonList);

    private final HashMap<Integer, ANodeView3D> idToNodeMap = new HashMap<>();
    private final HashMap<Integer, AEdgeView3D> idToEdgeMap = new HashMap<>();
    private final HashMap<Integer, ARibbonView3D> idToRibbonMap = new HashMap<>();

    public AGraphView3D() {
        this.getChildren().addAll(edgeViewGroup, nodeViewGroup, ribbonViewGroup);

        keepContainersUpdatedFromList();
    }

    /**
     *  creates and adds a node to the Graph
     * @param id Unique Identifier, shared with the model
     * @param x coordinate
     * @param y coordinate
     * @param z coordinate
     * @param radius radius of sphere
     * @return The newly created node
     */
    public ANodeView3D addNode(IntegerProperty id, double x, double y, double z, double radius) {
        ANodeView3D nodeView3D = new ANodeView3D(radius, x, y, z, drawModeUtil);
        nodeView3D.nodeIdProperty().bind(id);

        this.nodeList.add(nodeView3D);

        return nodeView3D;
    }

    public void removeNode(ANodeView3D nodeView3D) {
        this.nodeList.remove(nodeView3D);
    }

    public AEdgeView3D addEdge(IntegerProperty id, ANodeView3D startNode, ANodeView3D endNode) {
        AEdgeView3D edgeView3D = new AEdgeView3D(startNode, endNode, drawModeUtil);
        edgeView3D.edgeIdProperty().bind(id);

        this.edgeList.add(edgeView3D);

        return edgeView3D;
    }

    public void removeEdge(AEdgeView3D edgeView3D) {
        this.edgeList.remove(edgeView3D);
    }

    /**
     * add empty ribbon, without nodes yet
     * @param id
     * @param color
     */
    public ARibbonView3D addRibbon(IntegerProperty id, Color color, boolean visible){
        ARibbonView3D ribbon = new ARibbonView3D(id, color, visible);
        this.ribbonList.add(ribbon);
        return ribbon;
    }

    public void removeRibbon(ARibbonView3D ribbon) {
        this.ribbonList.remove(ribbon);
    }

    /**
     * return ANodeView3D object with id, or null if no such Node exists.
     * @param id
     * @return ANodeView3D or null
     */
    public ANodeView3D getViewNode(int id) {
        return idToNodeMap.get(id);
    }

    /**
     * return AEdgeView3D object with id, or null if no such Edge exists.
     * @param id
     * @return AEdgeVie3D or null
     */
    public AEdgeView3D getViewEdge(int id) {
        return idToEdgeMap.get(id);
    }

    public ObservableList<ANodeView3D> getUnmodifiableNodeList() {
        return this.unmodifiableNodeList;
    }

    public ObservableList<AEdgeView3D> getUnmodifiableEdgeList() {
        return this.unmodifiableEdgeList;
    }

    public ObservableList<ARibbonView3D> getUnmodifiableRibbonList() {
        return unmodifiableRibbonList;
    }

    public Point3D calculateCenterPoint3D(){
        double minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        double maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (ANodeView3D node : getUnmodifiableNodeList()) {
            minX = Math.min(minX, node.getTranslateX());
            minY = Math.min(minY, node.getTranslateY());
            minZ = Math.min(minZ, node.getTranslateZ());
            maxX = Math.max(maxX, node.getTranslateX());
            maxY = Math.max(maxY, node.getTranslateY());
            maxZ = Math.max(maxZ, node.getTranslateZ());
        }
        return new Point3D((maxX + minX) / 2, (maxY + minY) / 2, (maxZ + minZ) / 2);
    }

    public void centerGraphView() {
        TranslateTransition tt = new TranslateTransition();
        tt.setNode(this);
        Point3D center = this.calculateCenterPoint3D();
        tt.setToX(- center.getX());
        tt.setToY(- center.getY());
        tt.setToZ(- center.getZ());
        tt.setDuration(Duration.seconds(1));

        tt.play();
    }

    private void keepContainersUpdatedFromList() {

        this.nodeList.addListener((ListChangeListener<? super ANodeView3D>) n -> {
            while (n.next()) {
                if (n.wasAdded()) {
                    for (ANodeView3D nv : n.getAddedSubList()) {
                        this.idToNodeMap.put(nv.getNodeId(), nv);
                        this.nodeViewGroup.getChildren().add(nv);
                    }
                }
                if (n.wasRemoved()) {
                    for (ANodeView3D nv : n.getRemoved()) {
                        this.idToNodeMap.remove(nv.getNodeId());
                        this.nodeViewGroup.getChildren().remove(nv);
                    }
                }
            }
            this.centerGraphView();
        });

        this.edgeList.addListener((ListChangeListener<AEdgeView3D>) n -> {
            while (n.next()) {
                if (n.wasAdded()) {
                    for (AEdgeView3D ev : n.getAddedSubList()) {
                        this.idToEdgeMap.put(ev.getEdgeId(), ev);
                        this.edgeViewGroup.getChildren().add(ev);
                    }
                }
                if (n.wasRemoved()) {
                    for (AEdgeView3D ev : n.getRemoved()) {
                        this.idToEdgeMap.remove(ev.getEdgeId());
                        this.edgeViewGroup.getChildren().remove(ev);
                    }
                }
            }
        });

        this.ribbonList.addListener((ListChangeListener<ARibbonView3D>) n -> {
            while (n.next()){
                n.getAddedSubList().forEach(ribbon -> {
                    this.idToRibbonMap.put(ribbon.getRibbonId(), ribbon);
                    this.ribbonViewGroup.getChildren().add(ribbon);
                });
                n.getRemoved().forEach(ribbon -> {
                    this.idToRibbonMap.remove(ribbon.getRibbonId());
                    this.ribbonViewGroup.getChildren().remove(ribbon);
                });
            }
        });

    }

    public DrawModeUtil getDrawModeUtil() {
        return drawModeUtil;
    }

    public ARibbonView3D getRibbon(int residueId) {
        return idToRibbonMap.get(residueId);
    }
}
