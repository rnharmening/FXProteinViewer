package proteinModel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import util.DefaultHashMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Atom {

    public static final List<String> MAIN_CHAIN_NAMES = Arrays.asList(new String[] {"C", "CA", "N"});
    private static final int factor = 1;

    private static final AtomicInteger idCounter = new AtomicInteger();

    // For further information of PDB format, and why these fields are chosen for atom representation, see
    // ftp://ftp.wwpdb.org/pub/pdb/doc/format_descriptions/Format_v33_A4.pdf @ page 176

    private final int id = idCounter.getAndIncrement();

    private final IntegerProperty serial = new SimpleIntegerProperty();
    private final StringProperty atomName = new SimpleStringProperty();

    private final StringProperty element = new SimpleStringProperty();

    private final StringProperty residueName = new SimpleStringProperty();
    private final IntegerProperty residueId = new SimpleIntegerProperty();

    private final DoubleProperty x = new SimpleDoubleProperty(),
                                 y = new SimpleDoubleProperty(),
                                 z = new SimpleDoubleProperty();

    ObservableList<Bond> bonds = FXCollections.observableArrayList();

    public Atom(int serial, String atomName, String residueName, int residueId,
                double x, double y, double z, String element) {

        this.serial.set(serial);
        this.atomName.setValue(atomName);
        this.residueName.setValue(residueName);
        this.residueId.set(residueId);
        this.x.set(x*factor);
        this.y.set(y*factor);
        this.z.set(z*factor);
        this.element.setValue(element);
    }


    public int getId() {
        return id;
    }

    public int getSerial() {
        return serial.get();
    }

    public IntegerProperty serialProperty() {
        return serial;
    }

    public String getAtomName() {
        return atomName.get();
    }

    public StringProperty atomNameProperty() {
        return atomName;
    }

    public String getResidueName() {
        return residueName.get().toLowerCase();
    }

    public StringProperty residueNameProperty() {
        return residueName;
    }

    public int getResidueId() {
        return residueId.get();
    }

    public IntegerProperty residueIdProperty() {
        return residueId;
    }

    public String getElement() {
        return element.get();
    }

    public StringProperty elementProperty() {
        return element;
    }

    public double getX() {
        return x.get();
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public double getY() {
        return y.get();
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public double getZ() {
        return z.get();
    }

    public DoubleProperty zProperty() {
        return z;
    }

    // --------------------------------------
    // Static Objects - maybe in separate class?
    // --------------------------------------


    // the empirical values
    // from https://en.wikipedia.org/wiki/Atomic_radii_of_the_elements_(data_page)
    // where used, size in Angstrom

    private static final double scaleFactor = 1/200.0;
    public static final HashMap<String, Double> atomSizeMap = new DefaultHashMap<String, Double>((double) 0.7) {
        {
            put("H", 25.0 * scaleFactor);
            put("C", 70.0 * scaleFactor);
            put("N", 65.0 * scaleFactor);
            put("O", 60.0 * scaleFactor);
            put("S", 100.0 * scaleFactor);
        }
    };

    public ObservableList<Bond> getBonds() {
        return FXCollections.unmodifiableObservableList(bonds);
    }

    public Point3D getAtomPoint3D(){
        return new Point3D(x.getValue(), y.getValue(), z.getValue());
    }
}
