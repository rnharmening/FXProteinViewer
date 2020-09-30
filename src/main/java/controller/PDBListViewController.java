package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PDBListViewController {
    private ObservableList<String> data = FXCollections.observableArrayList();
    private FilteredList<String> filteredData = new FilteredList<>(data, null);

    private TextField filterField;
    private ListView listView;

    public PDBListViewController(TextField filterField, ListView listView) {
        this.filterField = filterField;
        this.listView = listView;
        listView.setItems(filteredData);

        filterField.textProperty().addListener(obs->{
            String filter = this.filterField.getText().toUpperCase();
            if(filter == null || filter.length() == 0) {
                filteredData.setPredicate(null);
            }
            else {
                List<String> filterList = Arrays.asList(filter.split("\\+"));
                filterList.forEach(e -> "".contains(e));
                filteredData.setPredicate(s -> containsAll(s, filterList));
            }
        });
    }

    private boolean containsAll(String word, List<String> subStrings){
        for (String subString : subStrings){
            if (!word.toLowerCase().contains(subString.toLowerCase())) {return false;}
        }
        return true;
    }


    public void clear(){
        data.clear();
    }

    public void setData(ObservableList<String> data){
        this.clear();
        data.forEach(d -> this.data.add(d.replace("\"", "")
                                         .replace(",", " ")));
    }

}
