package util;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ColorUtil {

    public static Color randomColor(){
        return Color.color(Math.random(), Math.random(), Math.random());
    }

    public static Color randomColor(double min, double max){
        double[] rgb = new double[]{Math.random(), Math.random(), Math.random()};
        for (int i = 0; i < rgb.length; i++) {
            double val = rgb[i];
            val = min > val ? min : val;
            val = max < val ? max : val;
            rgb[i] = val;
        }
        return Color.color(rgb[0], rgb[1], rgb[2]);
    }

    public static PhongMaterial randomMaterial(){
        return new PhongMaterial(randomColor());
    }

    private static final HashMap<String, Color> colorMapAtom = new DefaultHashMap<String, Color>(Color.DARKGREY) {
        {
            put("O", Color.RED);
            put("N", Color.BLUE);
            put("C", Color.rgb(70, 70, 70));
            put("H", Color.WHITE);
            put("S", Color.YELLOW);
        }
    };

    public static final Map<String, Color> COLOR_MAP_ATOM = Collections.unmodifiableMap(colorMapAtom);

    private static final HashMap<String, Color> colorMapResidue = new DefaultHashMap<String, Color>(Color.DARKGREY) {
        {
            put("ala", Color.rgb(  50, 20, 20));
            put("arg", Color.rgb(100, 20, 20));
            put("asn", Color.rgb(150, 20, 20));
            put("asp", Color.rgb(200, 20, 20));
            put("cys", Color.rgb(255, 20, 20));
            put("gln", Color.rgb(55, 150, 20));
            put("glu", Color.rgb(100, 155, 20));
            put("gly", Color.rgb(150, 155, 20));
            put("his", Color.rgb(200, 155, 20));
            put("ile", Color.rgb(255, 155, 20));
            put("leu", Color.rgb(55, 200, 150));
            put("lys", Color.rgb(100, 200, 155));
            put("met", Color.rgb(150, 200, 155));
            put("phe", Color.rgb(200, 200, 155));
            put("pro", Color.rgb(255, 200, 155));
            put("ser", Color.rgb(55, 100, 155));
            put("thr", Color.rgb(100,  100, 155));
            put("trp", Color.rgb(150, 100, 155));
            put("tyr", Color.rgb(200, 100, 155));
            put("val", Color.rgb(255, 100, 155));
        }
    };

    public static final Map<String, Color> COLOR_MAP_RESIDUE = Collections.unmodifiableMap(colorMapResidue);

    private static final HashMap<String, Color> colorMapSecStruc = new DefaultHashMap<String, Color>(Color.DARKGREY) {
        {
            put("helix", Color.YELLOW);
            put("sheet", Color.BLUE);
            put("loop", Color.LIGHTGRAY);
        }
    };

    public static final Map<String, Color> COLOR_MAP_SEC_STRUCTURE = Collections.unmodifiableMap(colorMapSecStruc);

}
