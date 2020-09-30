package PDB;

import proteinModel.Atom;
import proteinModel.Protein;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class PDBParser {

    // List of array containing [0] start and [1] end
    private final static ArrayList<Integer> helixIndices = new ArrayList<>();
    private final static ArrayList<Integer> sheetsIndices = new ArrayList<>();
    private final static ArrayList<Object[]> atoms = new ArrayList<>();

    public static ArrayList<Object[]> parse(BufferedReader br) throws IOException {

        atoms.clear();
        helixIndices.clear();
        sheetsIndices.clear();

        for (String line = br.readLine(); line != null; line=br.readLine()){
            if(line.startsWith("HELIX")){
                int initialResidueNr = Integer.parseInt(line.substring(21,25).replaceAll(" ", ""));
                int terminalResidueNr = Integer.parseInt(line.substring(33,37).replaceAll(" ", ""));
                // add all the indices to the helix array
                IntStream.range(initialResidueNr, terminalResidueNr+1).forEach(e -> helixIndices.add(e));
            }
            if(line.startsWith("SHEET")){
                int initialResidueNr = Integer.parseInt(line.substring(22,26).replaceAll(" ", ""));
                int terminalResidueNr = Integer.parseInt(line.substring(33,37).replaceAll(" ", ""));
                // Add all the indices to the sheet array
                IntStream.range(initialResidueNr, terminalResidueNr+1).forEach(e -> sheetsIndices.add(e));

            }
            if(line.startsWith("ATOM")){
                atoms.add(parseAtomLineToObjectArray(line));
            }
            if(line.startsWith("TER")){
                break;
            }
        }
        return atoms;
    }

    public static void parseToProteinModel(Protein proteinModel, BufferedReader br) throws IOException {
        parseToProteinModel(proteinModel, br, false);
    }


    public static void parseToProteinModel(Protein proteinModel, BufferedReader br, boolean clear) throws IOException {
        if(clear){proteinModel.clear();}

        for(Object[] atomFields : parse(br)){
            int serial = (int) atomFields[1];
            String atomName = (String) atomFields[2];
            String residue = (String) atomFields[4];
            int residueId = (int) atomFields[6];
            double x = (double) atomFields[8];
            double y = (double) atomFields[9];
            double z = (double) atomFields[10];
            String element = (String) atomFields[13];

            if(proteinModel.getResidue(residueId) == null){
                if( sheetsIndices.contains(residueId)){
                    proteinModel.addResidue(residue, residueId, "sheet");
                } else if (helixIndices.contains(residueId)){
                    proteinModel.addResidue(residue, residueId, "helix");
                } else {
                    proteinModel.addResidue(residue, residueId, "-loop");

                }
            }

            Atom atom = new Atom(serial, atomName, residue, residueId, x, y, z, element);

            if(atomFields[7].equals(' ') && " A".contains(String.valueOf(atomFields[3])))
                proteinModel.addAtom(atom);
        }
        proteinModel.connectAllResidues();
    }

    public static Object[] parseAtomLineToObjectArray(String line){
        int lineLength = line.length();

        for (int i = 0; i < 80-lineLength; i++){
            line = line + " ";
        }


        Object[] fields = new Object[16];

        // Line Name,               String          1 - 6
        fields[0] = subStringNoWhiteSpace(line, 0, 6);

        // Atom serial number,      Integer         7 - 11
        fields[1] = Integer.parseInt(subStringNoWhiteSpace(line, 6, 11));

        // Atom name,               String          13 - 16
        fields[2] = subStringNoWhiteSpace(line, 12, 16);

        // Alternate Location id,   Char            17
        fields[3] = line.charAt(16);

        // Residue Name,            String          18 - 20
        fields[4] = subStringNoWhiteSpace(line, 17, 20);

        // Chain ID,                Char            22
        fields[5] = line.charAt(21);

        // Residue seq number,      Integer         23 - 26
        fields[6] = Integer.parseInt(subStringNoWhiteSpace(line, 22, 26));

        // insertion Code,          Char            27
        fields[7] = line.charAt(26);

        // X coordinate,            Double(8.3)     31 - 38
        fields[8] = Double.parseDouble(subStringNoWhiteSpace(line, 30,38));

        // Y coordinate,            Double(8.3)     39 - 46
        fields[9] = Double.parseDouble(subStringNoWhiteSpace(line, 38,46));

        // Z coordinate,            Double(8.3)     47 - 54
        fields[10] = Double.parseDouble(subStringNoWhiteSpace(line, 46,54));

        // Occupancy,               Double(6.2)     55 - 60
        fields[11] = Double.parseDouble(subStringNoWhiteSpace(line, 54, 60));

        // Temperature factor,      Double(6.2)     61 - 66
        fields[12] = Double.parseDouble(subStringNoWhiteSpace(line, 60, 66));

        // Element symbol,          String      77 - 78
        fields[13] = subStringNoWhiteSpace(line, 76, 78);

        // Charge,                  String      79 - 80
        fields[14] = subStringNoWhiteSpace(line, 78, 80);

        // Complete Line,                    String
        fields[15] = line;
        return fields;
    }

    public static String subStringNoWhiteSpace(String s, int begin, int end){
        return removeWhiteSpace(s.substring(begin, end));
    }

    public static String removeWhiteSpace(String s){
        return s.replaceAll("[\\s|\\u00A0]+", "");
    }

    private static String lengthenString(String s, int minLength){
        StringBuilder sb = new StringBuilder(s);

        for (int i = 0; i < minLength-s.length(); i++){
            sb.append(" ");
        }

        return sb.toString();
    }

}
