package util;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class KeyCombinationCollection {

    public static final KeyCombination ctrlZ = new KeyCodeCombination(KeyCode.Z,
            KeyCombination.CONTROL_DOWN);
    public static final KeyCombination ctrlY = new KeyCodeCombination(KeyCode.Y,
            KeyCombination.CONTROL_DOWN);

    public static final KeyCombination ctrlA = new KeyCodeCombination(KeyCode.A,
            KeyCombination.CONTROL_DOWN);
    public static final KeyCombination ctrlN = new KeyCodeCombination(KeyCode.N,
            KeyCombination.CONTROL_DOWN);

    public static final KeyCombination ctrlPlus = new KeyCodeCombination(KeyCode.PLUS,
            KeyCombination.CONTROL_DOWN);
    public static final KeyCombination ctrlMinus = new KeyCodeCombination(KeyCode.MINUS,
            KeyCombination.CONTROL_DOWN);

    public static final KeyCombination shiftPlus = new KeyCodeCombination(KeyCode.PLUS,
            KeyCombination.SHIFT_DOWN);
    public static final KeyCombination shiftMinus = new KeyCodeCombination(KeyCode.MINUS,
            KeyCombination.SHIFT_DOWN);

}
