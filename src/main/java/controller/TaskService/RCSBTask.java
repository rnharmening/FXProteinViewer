package controller.TaskService;

import PDB.RCSBFetcher;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.util.List;

public class RCSBTask extends Task<List<String>> {
    @Override
    protected ObservableList<String> call() throws Exception {

        ObservableList<String> proteinList = RCSBFetcher.getProteinListFromRCSB();
        return proteinList;
    }
}
