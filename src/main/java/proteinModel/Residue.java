package proteinModel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.DefaultHashMap;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Residue {

    // Three letter residue Name
    private final StringProperty residueName = new SimpleStringProperty();
    // Id for the Residue in the Model
    private final IntegerProperty residueId = new SimpleIntegerProperty();

    private String secondaryStructure;

    private final ObservableList<Atom> residueAtoms = FXCollections.observableArrayList();
    private final ObservableList<Atom> unmodifiableAtoms = FXCollections.unmodifiableObservableList(residueAtoms);
    private final DefaultHashMap<String, Atom> atomNameMap = new DefaultHashMap<>(null);

    private final ObservableList<Atom> backBoneAtoms = FXCollections.observableArrayList();
    private final ObservableList<Atom> unmodifiableBackBoneAtoms = FXCollections.unmodifiableObservableList(backBoneAtoms);

    public Residue(String residueName, int residueId, String secondaryStructure) {
        this.residueId.set(residueId);
        this.residueName.setValue(residueName);
        this.secondaryStructure = secondaryStructure;
    }

    public Atom addAtom(Atom atom){
        this.residueAtoms.add(atom);
        this.atomNameMap.put(atom.getAtomName(), atom);
        if (Atom.MAIN_CHAIN_NAMES.contains(atom.getAtomName())){
            this.backBoneAtoms.add(atom);
        }
        return atom;
    }

    public void removeAtom(Atom atom){
        this.residueAtoms.remove(atom);
        this.backBoneAtoms.remove(atom);
        this.atomNameMap.remove(atom.getAtomName());
    }

    public int getResidueId(){
        return residueId.getValue();
    }

    public void connectMainChain(Protein protein) {
        String[][] connectionArray = AminoAcidConnectionMaps.residueToConnectionMap.get(this.residueName.get().toLowerCase());
        for(String[] connection : connectionArray){
            Atom atomA = atomNameMap.get(connection[0]);
            Atom atomB = atomNameMap.get(connection[1]);
            protein.addBond(atomA, atomB, BondType.Covalent);
        }
    }

    public void connectOxt(Protein protein) {
        Atom atomA = atomNameMap.get("OXT");
        Atom atomB = atomNameMap.get("C");
        protein.addBond(atomA, atomB, BondType.Covalent);
    }


    public Atom getAtom(String atomName){
        return this.atomNameMap.get(atomName);
    }

    public char getSingleCharRepresentation(){
        return singleRepAA.get(this.residueName.get().toLowerCase());
    }

    public char getSecondaryStructureCharRep(){
        return this.secondaryStructure.toUpperCase().charAt(0);
    }

    // Static Objects - maybe in separate class?
    // --------------------------------------

    // --------------------------------------

    public static final HashMap<String, Character> singleRepAA = new DefaultHashMap<String, Character>('?') {
        {
            put("ala", 'A');
            put("arg", 'R');
            put("asn", 'N');
            put("asp", 'D');
            put("cys", 'C');
            put("gln", 'Q');
            put("glu", 'E');
            put("gly", 'G');
            put("his", 'H');
            put("ile", 'I');
            put("leu", 'L');
            put("lys", 'K');
            put("met", 'M');
            put("phe", 'F');
            put("pro", 'P');
            put("ser", 'S');
            put("thr", 'T');
            put("trp", 'W');
            put("tyr", 'Y');
            put("val", 'V');
        }
    };

    public ObservableList<Atom> getUnmodifiableAtoms() {
        return unmodifiableAtoms;
    }

    public ObservableList<Atom> getBackBoneAtoms() {
        return unmodifiableBackBoneAtoms;

    }

    public String getSecondaryStructure() {
        return secondaryStructure;
    }

    public IntegerProperty residueIdProperty() {
        return residueId;
    }

    public String getResidueName() {
        return residueName.get().toLowerCase();
    }

    public StringProperty residueNameProperty() {
        return residueName;
    }
}
