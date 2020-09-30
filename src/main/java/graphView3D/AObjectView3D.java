package graphView3D;

import javafx.scene.Group;

public abstract class AObjectView3D extends Group{

    public void setVisibility(boolean isVisible){

        this.visibleProperty().setValue(isVisible);

        this.setPickOnBounds(isVisible);
        this.setMouseTransparent(!isVisible);

    }
}
