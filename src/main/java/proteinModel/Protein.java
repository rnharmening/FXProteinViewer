package proteinModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.HashMap;

public class Protein {
    private final ObservableList<Residue> residues = FXCollections.observableArrayList();
    private final ObservableList<Atom> atoms = FXCollections.observableArrayList();
    private final ObservableList<Bond> bonds = FXCollections.observableArrayList();

    private final ObservableList<Residue> unmodifiableResidues = FXCollections.unmodifiableObservableList(residues);
    private final ObservableList<Atom> unmodifiableAtoms = FXCollections.unmodifiableObservableList(atoms);
    private final ObservableList<Bond> unmodifiableBonds = FXCollections.unmodifiableObservableList(bonds);

    private final HashMap<Integer, Residue> residueMap = new HashMap<>();
    private final HashMap<Integer, Atom> atomMap = new HashMap<>();
    private final HashMap<Integer, Bond> bondMap= new HashMap<>();

    private final StringProperty sequence = new SimpleStringProperty();
    private final StringBuilder sequenceBuffer = new StringBuilder();

    public Protein() {
        keepMapsUpdated();
    }

    public Atom addAtom(Atom atom){
        this.atoms.add(atom);

        if(!residueMap.containsKey(atom.getResidueId())){
            Residue res = addResidue(atom.getResidueName(), atom.getResidueId(), "-loop");
            res.addAtom(atom);
        } else {
            residueMap.get(atom.getResidueId()).addAtom(atom);
        }
        return atom;
    }

    public Residue addResidue(String residueName, int residueId, String secondaryStructure){
        Residue res = new Residue(residueName, residueId, secondaryStructure);
        this.residues.add(res);
        this.sequenceBuffer.append(Residue.singleRepAA.get(residueName.toLowerCase()));
        this.sequence.setValue(sequenceBuffer.toString());
        return res;
    }

    public Bond addBond(Atom a, Atom b, BondType type){
        // Catch if atoms are the same
        if(a == null || b == null){return null;}

        Bond bond = new Bond(a, b, type);
        this.bonds.add(bond);
        a.bonds.add(bond);
        b.bonds.add(bond);
        return bond;
    }

    private void keepMapsUpdated() {
        residues.addListener((ListChangeListener<Residue>) n -> {
            while (n.next()) {
                n.getAddedSubList().forEach(res -> residueMap.put(res.getResidueId(), res));
                n.getRemoved().forEach(res -> residueMap.remove(res.getResidueId(), res));
            }
        });

        atoms.addListener((ListChangeListener<Atom>) n -> {
            while (n.next()) {
                n.getAddedSubList().forEach(atom -> atomMap.put(atom.getSerial(), atom));
                n.getRemoved().forEach(atom -> atomMap.remove(atom.getSerial(), atom));
            }
        });

        bonds.addListener((ListChangeListener<Bond>) n -> {
            while (n.next()){
                n.getAddedSubList().forEach(bond -> bondMap.put(bond.getId(), bond));
                n.getRemoved().forEach(bond -> bondMap.remove(bond.getId(), bond));
            }
        });
    }

    public ObservableList<Residue> getUnmodifiableResidues() {
        return unmodifiableResidues;
    }

    public ObservableList<Atom> getUnmodifiableAtoms() {
        return unmodifiableAtoms;
    }

    public ObservableList<Bond> getUnmodifiableBonds() {
        return unmodifiableBonds;
    }

    public Atom getAtom(int serial){
        return this.atomMap.get(serial);
    }

    public String getSequence(){
        return this.sequence.get();
    }

    public StringProperty sequenceProperty() {
        return sequence;
    }

    public void clear() {
        this.atoms.clear();
        this.bonds.clear();
        this.residues.clear();
        this.sequenceBuffer.setLength(0);
        this.sequence.setValue("");
    }

    public void connectAllResidues() {
        Residue lastRes = null;
        for(Residue res : this.unmodifiableResidues){
            res.connectMainChain(this);
            if (lastRes != null) {
                this.connectResidues(lastRes, res);
            }
            lastRes = res;
        }
        if(lastRes != null)
            lastRes.connectOxt(this);
    }

    public void connectResidues(Residue lastRes, Residue res) {
        this.addBond(lastRes.getAtom("C"), res.getAtom("N"), BondType.Covalent);
    }

    public Residue getResidue(int residueId) {
        return this.residueMap.get(residueId);
    }

    public Bond getBond(int bondId) { return this.bondMap.get(bondId);}
}

