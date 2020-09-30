package proteinModel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.concurrent.atomic.AtomicInteger;

public class Bond {

    private static final AtomicInteger idCounter = new AtomicInteger();
    private final IntegerProperty id = new SimpleIntegerProperty(idCounter.getAndIncrement());

    private Atom atomA, atomB;
    private BondType type;

    public Bond(Atom atomA, Atom atomB) {
        this.atomA = atomA;
        this.atomB = atomB;
    }

    public Bond(Atom atomA, Atom atomB, BondType type) {
        this.atomA = atomA;
        this.atomB = atomB;
        this.type = type;
    }

    public Atom getAtomA() {
        return atomA;
    }

    public Atom getAtomB() {
        return atomB;
    }

    public int getId() {
        return id.getValue();
    }

    public IntegerProperty idProperty() {
        return id;
    }
}
