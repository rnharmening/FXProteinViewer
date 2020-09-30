package proteinModel;

import java.util.HashMap;

/**
 * This class contains HashMaps for each Amino Acid, containing Key Value-Pairs of Atom Name - Atom Name,
 * representing the connection between two atoms
 */
public class AminoAcidConnectionMaps {


    private static final String[][] Alanine = {
            new String[]{"N", "CA"},
            new String[]{"N", "CA"},
            new String[]{"N", "CA"},
            new String[]{"CA", "CB"},
            new String[]{"CA", "C"},
            new String[]{"C", "O"}

    };

    private static final String[][] Arginine = {
            new String[]{"N", "CA"},
            new String[]{"CA", "CB"},
            new String[]{"CA", "C"},
            new String[]{"C", "O"},
            new String[]{"CB", "CG"},
            new String[]{"CG", "CD"},
            new String[]{"CD", "NE"},
            new String[]{"NE", "CZ"},
            new String[]{"CZ",  "NH1"},
            new String[]{"CZ",  "NH2"}
        };

    private static final String[][] AspAcid = {
            new String[]{"N", "CA"},
            new String[]{"CA", "CB"},
            new String[]{"CA", "C"},
            new String[]{"C", "O"},
            new String[]{"CB", "CG"},
            new String[]{"CG", "OD1"},
            new String[]{"CG", "OD2"},
            new String[]{"CG", "ND2"}
    };

    private static final String[][] Asparagine = {
            new String[]{"N", "CA"},
            new String[]{"CA", "CB"},
            new String[]{"CA", "C"},
            new String[]{"C", "O"},
            new String[]{"CB", "CG"},
            new String[]{"CG", "OD1"},
            new String[]{"CG", "OD2"},
            new String[]{"CG", "ND2"}
    };

    private static final String[][] Threonin = {
            new String[]{"N", "CA"},
            new String[]{"CA", "CB"},
            new String[]{"CA", "C"},
            new String[]{"C", "O"},
            new String[]{"CB", "CG2"},
            new String[]{"CB", "OG1"}
    };

    private static final String[][] Tryptophan = {
            new String[]{"N", "CA"},
            new String[]{"CA", "CB"},
            new String[]{"CA", "C"},
            new String[]{"C", "O"},
            new String[]{"CB", "CG"},
            new String[]{"CG", "CD1"},
            new String[]{"CG", "CD2"},
            new String[]{"CD1", "NE1"},
            new String[]{"NE1", "CE2"},
            new String[]{"CD2", "CE2"},
            new String[]{"CD2", "CE3"},
            new String[]{"CE3", "CZ3"},
            new String[]{"CZ3", "CH2"},
            new String[]{"CH2", "CZ2"},
            new String[]{"CZ2", "CE2"}
    };

    private static final String[][] Serine = {
            new String[]{"N", "CA"},
            new String[]{"CA", "CB"},
            new String[]{"CA", "C"},
            new String[]{"C", "O"},
            new String[]{"CB", "OG"}
    };

    private static final String[][] Cystein = {
            new String[]{"N", "CA"},
            new String[]{"CA", "CB"},
            new String[]{"CA", "C"},
            new String[]{"C", "O"},
            new String[]{"CB", "SG"}
    };

    private static final String[][] Proline = {
            new String[]{"N", "CA"},
            new String[]{"CA", "CB"},
            new String[]{"CA", "C"},
            new String[]{"C", "O"},
            new String[]{"CB", "CG"},
            new String[]{"CG", "CD"},
            new String[]{"CD", "N"}
    };

    private static final String[][] Leucine = {
            new String[]{"N", "CA"},
            new String[]{"CA", "CB"},
            new String[]{"CA", "C"},
            new String[]{"C", "O"},
            new String[]{"CB", "CG"},
            new String[]{"CG", "CD1"},
            new String[]{"CG", "CD2"}
    };

    private static final String[][] Isoleucine = {
            new String[]{"N", "CA"},
            new String[]{"CA", "CB"},
            new String[]{"CA", "C"},
            new String[]{"C", "O"},
            new String[]{"CB", "CG1"},
            new String[]{"CG1", "CD1"},
            new String[]{"CB", "CG2"}
    };

    private static final String[][] Glycin = {
            new String[]{"N", "CA"},
            new String[]{"CA", "C"},
            new String[]{"C", "O"}
    };

    private static final String[][] GlutamicAcid = {
            new String[]{"N", "CA"},
            new String[]{"CA", "CB"},
            new String[]{"CA", "C"},
            new String[]{"C", "O"},
            new String[]{"CB", "CG"},
            new String[]{"CB", "CG"},
            new String[]{"CG", "CD"},
            new String[]{"CD", "OE1"},
            new String[]{"CD", "OE2"}
    };

    private static final String[][] Glutamine = {
            new String[]{"N", "CA"},
            new String[]{"CA", "CB"},
            new String[]{"CA", "C"},
            new String[]{"C", "O"},
            new String[]{"CB", "CG"},
            new String[]{"CB", "CG"},
            new String[]{"CG", "CD"},
            new String[]{"CD", "OE1"},
            new String[]{"CD", "NE2"}
    };

    private static final String[][] Histidine = {
            new String[]{"N", "CA"},
            new String[]{"CA", "CB"},
            new String[]{"CA", "C"},
            new String[]{"C", "O"},
            new String[]{"CB", "CG"},
            new String[]{"CB", "NE1"},
            new String[]{"CG", "CD2"},
            new String[]{"CG", "ND1"},
            new String[]{"CD2", "NE2"},
            new String[]{"CE1", "NE2"},
            new String[]{"CE1", "ND1"},
    };

    private static final String[][] Lysin = {
            new String[]{"N", "CA"},
            new String[]{"CA", "CB"},
            new String[]{"CA", "C"},
            new String[]{"C", "O"},
            new String[]{"CB", "CG"},
            new String[]{"CG", "CD"},
            new String[]{"CD", "CE"},
            new String[]{"CE", "NZ"}
    };

    private static final String[][] Valine = {
            new String[]{"N", "CA"},
            new String[]{"CA", "CB"},
            new String[]{"CA", "C"},
            new String[]{"C", "O"},
            new String[]{"CB", "CG1"},
            new String[]{"CB", "CG2"}
    };

    private static final String[][] Methionine = {
            new String[]{"N", "CA"},
            new String[]{"CA", "CB"},
            new String[]{"CA", "C"},
            new String[]{"C", "O"},
            new String[]{"CB", "CG"},
            new String[]{"CG", "SD"},
            new String[]{"SD", "CE"}
    };

    private static final String[][] Tyrosine = {
            new String[]{"N", "CA"},
            new String[]{"CA", "CB"},
            new String[]{"CA", "C"},
            new String[]{"C", "O"},
            new String[]{"CB", "CG"},
            new String[]{"CG", "CD2"},
            new String[]{"CG", "CD1"},
            new String[]{"CD1", "CE1"},
            new String[]{"CD2", "CE2"},
            new String[]{"CE2", "CZ"},
            new String[]{"CE1", "CZ"},
            new String[]{"CZ", "OH"},
    };

    private static final String[][] Phanylalanine = {
            new String[]{"N", "CA"},
            new String[]{"CA", "CB"},
            new String[]{"CA", "C"},
            new String[]{"C", "O"},
            new String[]{"CB", "CG"},
            new String[]{"CG", "CD1"},
            new String[]{"CG", "CD2"},
            new String[]{"CD1", "CE1"},
            new String[]{"CD2", "CE2"},
            new String[]{"CE1", "CZ"},
            new String[]{"CE2", "CZ"}
    };




    public static final HashMap<String, String[][]> residueToConnectionMap =
            new HashMap<String, String[][]>() {
                {
                    put("thr", Threonin);
                    put("arg", Arginine);
                    put("asn", Asparagine);
                    put("asp", AspAcid);
                    put("ala", Alanine);
                    put("trp", Tryptophan);
                    put("pro", Proline);
                    put("ser", Serine);
                    put("leu", Leucine);
                    put("gly", Glycin);
                    put("glu", GlutamicAcid);
                    put("gln", Glutamine);
                    put("ile", Isoleucine);
                    put("cys", Cystein);
                    put("his", Histidine);
                    put("lys", Lysin);
                    put("met", Methionine);
                    put("phe", Phanylalanine);
                    put("tyr", Tyrosine);
                    put("val", Valine);
                }
            };



}
