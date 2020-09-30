package PDB;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RCSBFetcher {

    private static ObservableList<String> proteinPdbListSmall = FXCollections.observableArrayList();
    private static ObservableList<String> proteinPdbListComplete = FXCollections.observableArrayList();
    private static int start = 0;
    private static int step = 250;


    static final String RCSB_REPORT_URL = "https://www.rcsb.org/pdb/rest/customReport.csv?" +
            "pdbids=*&customReportColumns=structureId," +
            "entityMacromoleculeType," +
            "classification&service=wsfile" +
            "&format=csv";
    static final String PROTEIN_DECLARATION = "\"Polypeptide(L)\"";

    public static ObservableList<String> getProteinListFromRCSB() throws IOException {
        final ObservableList<String> lines = FXCollections.observableArrayList();

        for (String line : getListFromUrl(RCSB_REPORT_URL)){
            if (line.contains(PROTEIN_DECLARATION)){
                String[] splitLine = line.replace("\"", "").split(",");
                String l = new StringBuffer()
                        .append(splitLine[0])
                        .append(" - ")
                        .append(splitLine.length > 1 ? splitLine[1] : "")
                        .append(" - ")
                        .append(splitLine.length > 3 ? splitLine[3] : "").toString();
                lines.add(l);}
        }
        return lines;
    }

    /**
     * gets the result of a "GET" request
     * @param url
     * @return lines received
     * @throws IOException
     */
    public static List<String> getListFromUrl(String url) throws IOException {
        final HttpURLConnection connection=(HttpURLConnection)(new URL(url)).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        final List<String> lines = new ArrayList<>();
        try(BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = rd.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    public static ObservableList<String> getProteinPdbListSmall() {
        return proteinPdbListSmall;
    }

    public static ObservableList<String> getProteinPdbListComplete(){
        return proteinPdbListComplete;
    }

    /**
     * Adds new elements from complete list to small list, returns the fraction of the (old start/new start)
     * @return double in range(0, 1)
     */
    public static double increaseSmallProteinList(){
        int newLimit = (start+step) > proteinPdbListComplete.size() ? proteinPdbListComplete.size() : start + step;
        RCSBFetcher.getProteinPdbListSmall().addAll(proteinPdbListComplete.subList(start, newLimit));
        double ret = (start + step/2.0) / newLimit;
        start = newLimit;
        step = step < 2500 ?  step + 100: step;

        return ret;
    }

    public static void fetch() throws IOException {
        proteinPdbListComplete = FXCollections.observableArrayList(getProteinListFromRCSB());
        proteinPdbListSmall = FXCollections.observableArrayList(proteinPdbListComplete.subList(start, step));
    }
}
