package graphView3D;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;


public class ARibbonView3D extends AObjectView3D {

    private ObservableList<Point3D> point3DS = FXCollections.observableArrayList();

    private TriangleMesh triangleMesh = new TriangleMesh();
    private MeshView meshView = new MeshView();

    private IntegerProperty id = new SimpleIntegerProperty();

    public ARibbonView3D(IntegerProperty id, Color color){
        this(id, color, true);
    }

    public ARibbonView3D(IntegerProperty id, Color color, boolean visible) {
        this.id.bind(id);

        this.meshView.setDrawMode(DrawMode.FILL);
        this.meshView.setMaterial(new PhongMaterial(color));
        this.triangleMesh.getTexCoords().addAll(0, 0);

        this.getChildren().add(this.meshView);
        this.visibleProperty().setValue(visible);

        setUpNewPointListeners();
    }

    private void setUpNewPointListeners() {
        this.point3DS.addListener((ListChangeListener<Point3D>) c -> {
            while (c.next()) {
                c.getAddedSubList().forEach(point -> {

                    // Add coordinates of new point to triangleMesh Points
                    this.triangleMesh.getPoints().addAll(
                            (float) point.getX(), (float) point.getY(), (float) point.getZ());

                    // If size of points is larger than 2, we need to add a new face (or two)
                    if (this.triangleMesh.getPoints().size() / 3 >= 3) {
                        includeLatestPointIntoMeshFace();
                    }
                });
            }
        });

        // Update MeshView with Mesh, is this necessary?
        this.meshView.setMesh(this.triangleMesh);
    }


    private void includeLatestPointIntoMeshFace() {
        // If size of points is larger than 3, we need to add a new face (or two)
        if (this.triangleMesh.getPoints().size()/3 >= 3){
            int i = this.triangleMesh.getPoints().size()/3 - 1;
            int[] faces = new int[] {
                    i, 0,   i - 1, 0,   i - 2, 0, // Forward Face
                    i, 0,   i - 2, 0,   i - 1, 0  // Reverse Face
            };
            this.triangleMesh.getFaces().addAll(faces);
            this.triangleMesh.getFaceSmoothingGroups().addAll(i%2, (i+1)%2);
        }
    }

    public void addPoint(Point3D point) {
        this.point3DS.add(point);
    }

    public void setColor(Color color) {
        this.meshView.setMaterial(new PhongMaterial(color));
    }

    public int getRibbonId(){
        return this.id.getValue();
    }

}