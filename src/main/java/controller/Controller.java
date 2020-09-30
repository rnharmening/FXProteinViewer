package controller;

import PDB.PDBParser;
import controller.TaskService.RCSBService;
import graphView3D.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.PDBViewer;
import proteinModel.*;
import util.ColorUtil;
import visualisations.AminoAcidBarChart;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Controller extends VBox implements Initializable {

    // Non FXML Objects ##########

    private final RCSBService rcsbService = new RCSBService();

    private final Protein proteinModel = new Protein();
    private final AGraphView3D graphView3D = new AGraphView3D();
    private final Group subSceneContentGroup = new Group(graphView3D);

    private Property<Transform> viewTransformProperty = new SimpleObjectProperty<>(new Rotate());

    private final SubSceneEventHandler subSceneEventHandler =
            new SubSceneEventHandler(graphView3D, viewTransformProperty);
    // private final CommandManager commandManager = new CommandManager();

    private ASelectionModel<Residue> residueSelection = new ASelectionModel<Residue>();
    private BoundingBoxes2D boundingBoxes2D;

    private final HashMap<Residue, Label[]> residueLabelHashMap = new HashMap<>();

    private PDBListViewController pdbListViewController;

    // FXML injections: ##########

    @FXML
    private MenuItem openFileMenuItem, reinitializeMenuItem, quitMenuItem,
            selectAllMenuItem, unselectAllMenuItem,
            mainAtomsDrawMenuItem, spaceFillingDrawMenuItem, tubeDrawMenuItem, secondaryStructureDrawMenuItem,
            secondaryColorMenuItem, aminoAcidColorMenuItem, mainAtomColorMenuItem, visBarChartMenuItem;

    @FXML
    private ToggleGroup colorModeToggleGroup;

    @FXML // dio buttons for the ColorMode
    private RadioButton atomTypeColorModeRB, aminoAcidColorModeRB, secStructureColorModeRB;

    @FXML
    private ToggleGroup drawModeToggleGroup;

    @FXML // Radio Buttons for the Draw Mode
    private RadioButton ballsAndSticksDrawModeRB, spaceFillDrawModeRB, tubeDrawModeRB, cartoonDrawModeRB;

    @FXML // Radio Button for hiding Side Chains
    private RadioButton showSideChainsRB;

    @FXML
    private Label pdbStatusLabel;

    @FXML
    private Button pdbEntryLoadButton, rcsbFetchButton;

    @FXML
    private ListView<String> pdbEntriesListView;

    @FXML
    private TextField searchPDBTextField;

    @FXML
    private SubScene subScene3D;

    private Camera camera3D;

    @FXML
    private Pane bottomPane, topPane;

    @FXML
    private ScrollPane sequenceScrollPane;
    @FXML
    private HBox detailHBox, hBoxSequenceWithLabelHBox, detailSecondaryStructureLabelHBox,
            detailSequenceHBox, detailSecondaryStructureHBox;
    @FXML
    private VBox detailLabelVBox, detailSequenceVBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        pdbListViewController = new PDBListViewController(searchPDBTextField, pdbEntriesListView);

        camera3D = new PerspectiveCamera(true);
        camera3D.setNearClip(0.1);
        camera3D.setFarClip(6000);

        camera3D.setTranslateZ(-100);
        setUpDisplayBindings();

        subScene3D.widthProperty().bind(bottomPane.widthProperty());
        subScene3D.heightProperty().bind(bottomPane.heightProperty());

        subScene3D.setCamera(camera3D);
        subScene3D.setRoot(subSceneContentGroup);
        setUpViewTransformListener();
        subSceneEventHandler.setupSubSceneEvents(subScene3D, camera3D);

        setUpProteinListener();
        setUpDrawModeRadioButtonListener();
        setUpColorModeRadioButtonListeners();
        setUpSelection();

        // setup the listeners for the RCSB Service
        setUpRCSBServiceListeners();
    }

     // ##################################################################
    // SetUp functions, which setup different view, model or gui listeners
    // ##################################################################

    /**
     * Keep service status displayed on gui
     */
    private void setUpRCSBServiceListeners() {

        pdbEntryLoadButton.disableProperty().bind(Bindings.not(pdbEntriesListView.getSelectionModel().selectedItemProperty().isNotNull()));

        rcsbService.setOnScheduled(v -> {
            pdbStatusLabel.setText("loading PDB entries...");
            pdbStatusLabel.setTextFill(Color.DARKCYAN);
            rcsbFetchButton.setDisable(true);
        });
        rcsbService.setOnSucceeded(v -> {
            pdbStatusLabel.setText("PDB entries Loaded.");
            pdbStatusLabel.setTextFill(Color.GREEN);
            rcsbFetchButton.setDisable(false);
            //pdbEntryLoadButton.setDisable(false);
            pdbListViewController.setData(FXCollections.observableArrayList(rcsbService.getValue()));
            searchPDBTextField.setDisable(false);
        });
        rcsbService.setOnFailed(v -> {
            pdbStatusLabel.setText("PDB Fetching Failed.");
            pdbStatusLabel.setTextFill(Color.RED);
            rcsbFetchButton.setDisable(false);
            System.err.println(rcsbService.getException().getMessage());
        });
    }

    // adds listener to update group orientation on viewTransform change
    protected void setUpViewTransformListener() {
        viewTransformProperty.addListener((c, o, n) ->{
            this.graphView3D.getTransforms().setAll(viewTransformProperty.getValue());
        });
    }


    /**
     *  Sets up listeners for each button of the Toggle Group to change Color Mode
     */
    private void setUpColorModeRadioButtonListeners() {

        atomTypeColorModeRB.selectedProperty().addListener(c -> {
            if (atomTypeColorModeRB.isSelected()){
                for(Atom atom : proteinModel.getUnmodifiableAtoms()){
                    graphView3D.getViewNode(atom.getSerial()).setColor(ColorUtil.COLOR_MAP_ATOM.get(atom.getElement()));
                }
                for(Bond bond : proteinModel.getUnmodifiableBonds()){
                    Atom atomA = bond.getAtomA();
                    Atom atomB = bond.getAtomB();
                    graphView3D.getViewEdge(bond.getId()).changeColor(
                            ColorUtil.COLOR_MAP_ATOM.get(atomA.getElement()),
                            ColorUtil.COLOR_MAP_ATOM.get(atomB.getElement())
                    );
                }
                for (Residue res : proteinModel.getUnmodifiableResidues()) {
                    Color color = ColorUtil.COLOR_MAP_SEC_STRUCTURE.get(res.getSecondaryStructure());
                    try {
                        graphView3D.getRibbon(res.getResidueId()).setColor(color);
                    } catch (NullPointerException e) {

                    }
                }
            }
        });

        aminoAcidColorModeRB.selectedProperty().addListener(c -> {
            if (aminoAcidColorModeRB.isSelected()){
                for(Atom atom : proteinModel.getUnmodifiableAtoms()){
                    graphView3D.getViewNode(atom.getSerial()).setColor(ColorUtil.COLOR_MAP_RESIDUE.get(atom.getResidueName()));
                }
                for(Bond bond : proteinModel.getUnmodifiableBonds()){
                    Atom atomA = bond.getAtomA();
                    Atom atomB = bond.getAtomB();
                    graphView3D.getViewEdge(bond.getId()).changeColor(
                            ColorUtil.COLOR_MAP_RESIDUE.get(atomA.getResidueName()),
                            ColorUtil.COLOR_MAP_RESIDUE.get(atomB.getResidueName())
                    );
                }
                for(Residue res : proteinModel.getUnmodifiableResidues()) {
                    ARibbonView3D ribbon = graphView3D.getRibbon(res.getResidueId());
                    if(ribbon != null) {ribbon.setColor(ColorUtil.COLOR_MAP_RESIDUE.get(res.getResidueName()));}
                }
            }
        });

        secStructureColorModeRB.selectedProperty().addListener(c -> {
            if (secStructureColorModeRB.isSelected()) {
                for (Residue res : proteinModel.getUnmodifiableResidues()) {
                    Color color = ColorUtil.COLOR_MAP_SEC_STRUCTURE.get(res.getSecondaryStructure());

                    ARibbonView3D ribbon = graphView3D.getRibbon(res.getResidueId());
                    if(ribbon != null) {ribbon.setColor(color);}

                    for (Atom  atom : res.getUnmodifiableAtoms()){
                        graphView3D.getViewNode(atom.getSerial()).setColor(color);

                        for (Bond bond : atom.getBonds()) {
                            graphView3D.getViewEdge(bond.getId()).changeColor(
                                    color, color
                            );
                        }
                    }
                }
            }
        });
    }

    /**
     *  Sets up listeners for each button of the Toggle Group to change Draw Mode
     */
    private void setUpDrawModeRadioButtonListener() {

        DrawModeUtil dmu = graphView3D.getDrawModeUtil();

        // BallsAndSticks Mode
        ballsAndSticksDrawModeRB.selectedProperty().addListener(c -> {
            if (ballsAndSticksDrawModeRB.isSelected()){
            } else {
                dmu.reset();
            }
        });

        // SpaceFilling Mode
        spaceFillDrawModeRB.selectedProperty().addListener(c -> {
            if (spaceFillDrawModeRB.isSelected()){
                dmu.setRadiusMultiplierProperty(8);
            } else {
                dmu.reset();
            }
        });

        // Ribbon draw mode
        tubeDrawModeRB.selectedProperty().addListener(c -> {
            if (tubeDrawModeRB.isSelected()){
                dmu.setIncludeAtomSizeProperty(false);
                dmu.setBondRadiusMultiplierProperty(0.99);
                dmu.setRadiusMultiplierProperty(0.2);
            } else {
                dmu.reset();
            }
        });

        showSideChainsRB.selectedProperty().addListener(c -> {
            if (!showSideChainsRB.isSelected()){
                graphView3D.getUnmodifiableNodeList().forEach(n -> n.setVisibility(false));
                graphView3D.getUnmodifiableEdgeList().forEach(e -> e.setVisibility(false));
                for (Atom atom : proteinModel.getUnmodifiableAtoms()){
                    if (Atom.MAIN_CHAIN_NAMES.contains(atom.getAtomName())){
                        graphView3D.getViewNode(atom.getSerial()).setVisibility(true);
                    }
                }
                for (Bond bond : proteinModel.getUnmodifiableBonds()){
                    if (Atom.MAIN_CHAIN_NAMES.contains(bond.getAtomA().getAtomName()) &&
                            Atom.MAIN_CHAIN_NAMES.contains(bond.getAtomB().getAtomName())){
                        graphView3D.getViewEdge(bond.getId()).setVisibility(true);

                    }
                }
            } else {
                graphView3D.getUnmodifiableNodeList().forEach(n -> n.setVisibility(true));
                graphView3D.getUnmodifiableEdgeList().forEach(e -> e.setVisibility(true));
            }
        });

        cartoonDrawModeRB.selectedProperty().addListener(c -> {
            if (cartoonDrawModeRB.isSelected()){

                showSideChainsRB.setSelected(false);
                showSideChainsRB.setDisable(true);

                dmu.setIncludeAtomSizeProperty(false);
                dmu.setBondRadiusMultiplierProperty(1);
                dmu.setRadiusMultiplierProperty(0.1);

                proteinModel.getUnmodifiableResidues().forEach(residue -> {
                    if ("SH".contains(String.valueOf(residue.getSecondaryStructureCharRep()))){
                        // If a ribbon exists, hide the residue atoms and bonds
                        residue.getBackBoneAtoms().forEach(atom -> {
                            atom.getBonds().forEach(bond -> {
                                graphView3D.getViewEdge(bond.getId()).setVisibility(false);
                            });
                            graphView3D.getViewNode(atom.getSerial()).setVisibility(false);
                        });
                        // and show the ribbon
                        ARibbonView3D ribbon = graphView3D.getRibbon(residue.getResidueId());
                        if (ribbon != null) {
                            ribbon.setVisibility(true);
                        }
                    }
                });
            } else {
                showSideChainsRB.setSelected(true);
                showSideChainsRB.setDisable(false);
                proteinModel.getUnmodifiableResidues().forEach(residue -> {
                    ARibbonView3D ribbon = graphView3D.getRibbon(residue.getResidueId());
                    if (ribbon != null) {
                        ribbon.setVisibility(false);
                    }
                });
                dmu.reset();
            }
        });
    }

    private void setUpDisplayBindings() {
        subScene3D.widthProperty().bind(bottomPane.widthProperty());
        subScene3D.heightProperty().bind(bottomPane.heightProperty());
    }

    private void setUpSelection() {
        // create BoundingBox Group
        this.boundingBoxes2D =
                new BoundingBoxes2D(bottomPane, graphView3D, residueSelection,
                        this.graphView3D.rotateProperty(),
                        this.viewTransformProperty,
                        this.subScene3D.widthProperty(),
                        this.subScene3D.heightProperty(),
                        this.subScene3D.getCamera().translateXProperty(),
                        this.subScene3D.getCamera().translateYProperty(),
                        this.subScene3D.getCamera().translateZProperty(),
                        this.spaceFillDrawModeRB.selectedProperty(),
                        this.ballsAndSticksDrawModeRB.selectedProperty(),
                        this.tubeDrawModeRB.selectedProperty(),
                        this.cartoonDrawModeRB.selectedProperty());
        this.boundingBoxes2D.setPickOnBounds(false);

        // Add BondingBoxes to TopPane
        topPane.getChildren().add(this.boundingBoxes2D);

        // Label Highlighting listener
        residueSelection.getSelectedItems().addListener((ListChangeListener<Residue>) c -> {
            while (c.next()) {
                c.getAddedSubList().forEach(res -> {
                    Background background = new Background(new BackgroundFill(Color.HOTPINK, CornerRadii.EMPTY, new Insets(0.0)));
                    for (Label label : residueLabelHashMap.get(res)){
                        label.setBackground(background);
                    }
                });
                c.getRemoved().forEach(res ->{
                    for (Label label : residueLabelHashMap.get(res)){
                        label.setBackground(Background.EMPTY);
                    }
                } );
            }
        });
    }


    /**
     * Setup the link between the Protein Model and the GraphView model
     * also includes listeners on view elements
     */
    private void setUpProteinListener() {

        // Event Listener for added Atoms
        this.proteinModel.getUnmodifiableAtoms().addListener((ListChangeListener<Atom>) n -> {
            while (n.next()) {
                n.getAddedSubList().forEach(atom -> {
                    ANodeView3D nodeView = this.graphView3D.addNode(atom.serialProperty(),
                            atom.getX(), atom.getY(), atom.getZ(), Atom.atomSizeMap.get(atom.getElement()));
                    nodeView.translateXProperty().bindBidirectional(atom.xProperty());
                    nodeView.translateYProperty().bindBidirectional(atom.yProperty());
                    nodeView.translateZProperty().bindBidirectional(atom.zProperty());
                    Tooltip t = new Tooltip(String.format("%s-%d : %s", atom.getResidueName(),
                            atom.getResidueId(),
                            atom.getAtomName()));
                    Tooltip.install(nodeView, t);
                    nodeView.setColor(ColorUtil.COLOR_MAP_ATOM.get(atom.getElement()));

                    if (Atom.MAIN_CHAIN_NAMES.contains(atom.getAtomName())){
                        ARibbonView3D ribbon = graphView3D.getRibbon(atom.getResidueId());
                        if ( ribbon != null) {

                            // If we start a new Ribbon (N), include connection to last residue on the C atom
                            if (atom.getAtomName().equals("N") && atom.getResidueId() > 1) {
                                Residue previousRes = proteinModel.getResidue(atom.getResidueId()-1);
                                if(previousRes != null) {
                                    Atom lastC = previousRes.getAtom("C");
                                    if(lastC != null)
                                        ribbon.addPoint(new Point3D(lastC.getX(), lastC.getY(), lastC.getZ()));
                                }
                            }

                            // Internal connection
                            ribbon.addPoint(new Point3D(atom.getX(), atom.getY(), atom.getZ()));
                        }

                        // If we open a new Residue, include N into PREVIOUS ribbon, if there is a previous ribbon
                        if (atom.getAtomName().equals("N") && atom.getResidueId() > 1){
                            Residue previousRes = proteinModel.getResidue(atom.getResidueId()-1);

                            ARibbonView3D previousRibbon =
                                    previousRes == null ? null :graphView3D.getRibbon(previousRes.getResidueId());

                            if (previousRibbon != null) {
                                previousRibbon.addPoint(new Point3D(atom.getX(), atom.getY(), atom.getZ()));
                            }
                        }
                    }

                    // Add on click selection handler
                    nodeView.setOnMouseClicked(residueSelectionEventHandler);
                });
                n.getRemoved().forEach(atom -> graphView3D.removeNode(graphView3D.getViewNode(atom.getSerial())));
            }
        });

        // Event listener for added Bonds
        this.proteinModel.getUnmodifiableBonds().addListener((ListChangeListener<Bond>) n -> {
            while (n.next()) {
                n.getAddedSubList().forEach(bond -> {
                    ANodeView3D nodeA = graphView3D.getViewNode(bond.getAtomA().getSerial());
                    ANodeView3D nodeB = graphView3D.getViewNode(bond.getAtomB().getSerial());
                    AEdgeView3D edge = graphView3D.addEdge(bond.idProperty(), nodeA, nodeB);
                    edge.setOnMouseClicked(residueSelectionEventHandler);
                });
                n.getRemoved().forEach(bond -> {
                    graphView3D.removeEdge(graphView3D.getViewEdge(bond.getId()));
                });
            }
        });


        // Listen on Residue list for the ribbon integration, as well as for the AminoAcid sequence labels
        this.proteinModel.getUnmodifiableResidues().addListener((ListChangeListener<Residue>) n -> {
            residueSelection.setItems(proteinModel.getUnmodifiableResidues().toArray(new Residue[0]));
            while (n.next()) {
                n.getAddedSubList().forEach(residue -> {

                    // If  is secondary structure, add ribbon
                    if("SH".contains(String.valueOf(residue.getSecondaryStructureCharRep()))){
                        ARibbonView3D ribbon = graphView3D.addRibbon(residue.residueIdProperty(),
                                ColorUtil.COLOR_MAP_SEC_STRUCTURE.get(residue.getSecondaryStructure()),
                                cartoonDrawModeRB.isSelected());
                        ribbon.setOnMouseClicked(residueSelectionEventHandler);
                    }
                    addLabelsToHboxes(residue);
                });
                n.getRemoved().forEach(residue -> {
                    Label[] labelsToRemove = residueLabelHashMap.get(residue);
                    detailSequenceHBox.getChildren().remove(labelsToRemove[0]);
                    detailSecondaryStructureHBox.getChildren().remove(labelsToRemove[1]);
                    residueLabelHashMap.remove(residue);

                    graphView3D.removeRibbon(graphView3D.getRibbon(residue.getResidueId()));
                });
            }
        });
    }
    // ######################################################################################################
    // non Setup functions
    // ######################################################################################################

    /**
     * Method which creates a Label with Monospaced and defined width
     * @return Label
     */
    private Label createCharLabel(char character, Object userData){
        Label label = new Label("" + character);
        label.setFont(Font.font("Monospaced"));
        label.setMinWidth(8);
        label.setMaxWidth(8);
        label.setUserData(userData);
        return label;
    }

    /**
     * Creates AmioAcid Char rep label as well as secondary structure label
     * @param residue
     */
    private void addLabelsToHboxes(Residue residue){
        Label resiudueLabel = createCharLabel(residue.getSingleCharRepresentation(), residue);

        resiudueLabel.setOnMouseClicked(residueSelectionEventHandler);
        Label secStructureLabel = createCharLabel(residue.getSecondaryStructureCharRep(), residue);

        secStructureLabel.setOnMouseClicked(residueSelectionEventHandler);
        residueLabelHashMap.put(residue, new Label[] {resiudueLabel, secStructureLabel});
        detailSequenceHBox.getChildren().add(resiudueLabel);
        detailSecondaryStructureHBox.getChildren().add(secStructureLabel);
    }


    // ######################################################################################################
    // FXML Functions, and helper functions of these
    // ######################################################################################################

    @FXML
    private void quitMenuItemEvent(ActionEvent event) {
        System.err.println("Exit");
        Platform.exit();
    }

    @FXML
    void colorModeChangeEvent(ActionEvent event) {
        if(event.getSource().equals(aminoAcidColorMenuItem)){
            aminoAcidColorModeRB.setSelected(true);
        }
        if(event.getSource().equals(secondaryColorMenuItem)){
            secStructureColorModeRB.setSelected(true);
        }
        if(event.getSource().equals(mainAtomColorMenuItem)){
            atomTypeColorModeRB.setSelected(true);
        }
    }

    @FXML
    void drawModeChangeEvent(ActionEvent event) {
        if(event.getSource().equals(tubeDrawMenuItem)){
            tubeDrawModeRB.setSelected(true);
        }
        if(event.getSource().equals(mainAtomsDrawMenuItem)){
            ballsAndSticksDrawModeRB.setSelected(true);
        }
        if(event.getSource().equals(spaceFillingDrawMenuItem)){
            spaceFillDrawModeRB.setSelected(true);
        }
        if(event.getSource().equals(secondaryStructureDrawMenuItem)){
            cartoonDrawModeRB.setSelected(true);
        }
    }

    @FXML
    void createChartEvent(ActionEvent event){
        if(event.getSource().equals(visBarChartMenuItem)){
            AminoAcidBarChart barChart = new AminoAcidBarChart(proteinModel, 800, 600);
            barChart.show(new Stage());
        }
    }

    private void resetBeforeNewProteinLoaded(){
        this.residueSelection.clearSelection();
        this.viewTransformProperty.setValue(new Rotate());
    }

    @FXML
    void loadEntryEvent(ActionEvent event) {
        String selectedItem = (String) pdbEntriesListView.getSelectionModel().getSelectedItem();
        String pdbId = selectedItem.split(" - ")[0].replace(" ", "");
        try {

            URL url = new URL(String.format("https://files.rcsb.org/download/%s.pdb", pdbId));
            InputStream is = url.openStream();

            reinitialize();
            resetBeforeNewProteinLoaded();

            PDBParser.parseToProteinModel(this.proteinModel, new BufferedReader(new InputStreamReader(is)));
            // PDBParser.parseToProteinModel(this.proteinModel, new BufferedReader(new URLReader(url)), true);
            System.err.println("Loaded entry " + pdbId);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @FXML
    void openFileEvent(ActionEvent event) {
        reinitialize();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.setTitle("Open pdb file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDB (*.pdb)", "*.pdb"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File selectedFile = fileChooser.showOpenDialog(PDBViewer.getPrimaryStage());

        if (selectedFile != null) {
            resetBeforeNewProteinLoaded();
            try {
                PDBParser.parseToProteinModel(
                        this.proteinModel, new BufferedReader(new FileReader(selectedFile)), true);
                System.out.println("Model loaded: " + selectedFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void reinitializeEvent(ActionEvent event) {
        reinitialize();
    }

    void reinitialize(){
        residueSelection.clearSelection();
        this.proteinModel.clear();
        atomTypeColorModeRB.setSelected(true);
        ballsAndSticksDrawModeRB.setSelected(true);
        showSideChainsRB.setSelected(true);
    }

    @FXML
    void selectAllEvent(ActionEvent event) {
        residueSelection.selectAll();
    }

    @FXML
    void selectNoneEvent(ActionEvent event) {
        residueSelection.clearSelection();
    }

    private EventHandler<MouseEvent> residueSelectionEventHandler =
            new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    Object source = t.getSource();
                    Residue selectedResidue = null;
                    if (!t.isShiftDown()){
                        residueSelection.clearSelection();
                    }

                    if (source instanceof ANodeView3D){
                        ANodeView3D nodeView = (ANodeView3D) source;
                        Atom clickedAtom = proteinModel.getAtom(nodeView.getNodeId());
                        selectedResidue = proteinModel.getResidue(clickedAtom.getResidueId());
                    } else if (source instanceof Label){
                        Residue residue = (Residue)((Label) source).getUserData();
                        selectedResidue = proteinModel.getResidue(residue.getResidueId());
                    } else if (source instanceof ARibbonView3D){
                        selectedResidue = proteinModel.getResidue(((ARibbonView3D) source).getRibbonId());
                    } else if (source instanceof AEdgeView3D) {
                        AEdgeView3D edge = (AEdgeView3D) source;
                        Bond bond = proteinModel.getBond(edge.getEdgeId());
                        selectedResidue = proteinModel.getResidue(bond.getAtomA().getResidueId());
                    }

                    if (selectedResidue != null){

                        residueSelection.select(selectedResidue);

                        if (!(source instanceof Label)){
                            // Scroll Sequence Scroll Pane
                            int residueIndex = proteinModel.getUnmodifiableResidues().indexOf(selectedResidue);
                            double position = residueIndex/((double)proteinModel.getUnmodifiableResidues().size()-1);
                            sequenceScrollPane.setHvalue(position);
                        }
                    }
                }
            };

    @FXML
    public void loadRCSBEntries(ActionEvent actionEvent) {
        rcsbService.restart();
    }
}
