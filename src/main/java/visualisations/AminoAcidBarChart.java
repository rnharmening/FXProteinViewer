package visualisations;

import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import proteinModel.Protein;
import util.DefaultHashMap;

import java.util.HashMap;

public class AminoAcidBarChart {

    private Protein proteinModel;

    private BarChart chart;
    private int width, height;

    public AminoAcidBarChart(Protein proteinModel, int width, int height) {
        this.proteinModel = proteinModel;
        this.width = width;
        this.height = height;

        createBarChart();
    }

    public void recalculateChart(){
        createBarChart();
    }

    /**
     * Creates the bar chart based on the aminoacid frequency of the proteinModel
     */
    private void createBarChart(){
        HashMap<Character, Double>  frequencies = countFrequency(proteinModel.getSequence());

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        this.chart = new BarChart<String, Number>(xAxis, yAxis);
        this.chart.setTitle("Amino Acid Frequencies");
        this.chart.setBarGap(2);
        xAxis.setLabel("Amino Acid");
        yAxis.setLabel("Percent");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        frequencies.forEach((Character key, Number value) -> {
            System.err.println(key);
            series.getData().add(new XYChart.Data<String, Number>(String.valueOf(key), value));
        });

        this.chart.getData().addAll(series);
        this.chart.setLegendVisible(false);
    }

    /**
     * count the frequency  (0-1) of each char in input String
     * @param sequence
     * @return HashMap containing frequency of each char in input string
     */
    private HashMap<Character, Double> countFrequency(String sequence){
        System.err.println(sequence);
        HashMap<Character, Double> countHashMap = new DefaultHashMap<>(0.0);

        for (char c : sequence.toCharArray()){
            countHashMap.put(c, countHashMap.get(c) + 1);
        }

        countHashMap.forEach((key, value) -> {
            countHashMap.put(key, 100.0 * ((Double)value)/sequence.length());
        });

        return countHashMap;
    }

    public void show(Stage stage){
        if (this.chart != null){
            Scene scene = new Scene(this.chart, this.width, this.height);
            stage.setScene(scene);
            stage.show();
        }

    }

}
