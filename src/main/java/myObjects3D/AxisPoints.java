package myObjects3D;

import javafx.geometry.Point3D;

public class AxisPoints {
    public static final Point3D ORIGIN = new Point3D(0, 0, 0);
    public static final Point3D X_AXIS = new Point3D(1, 0, 0);
    public static final Point3D Y_AXIS = new Point3D(0, 1, 0);
    public static final Point3D Z_AXIS = new Point3D(0, 0, 1);

    public static Point3D randomAxis() {
        double x = Math.random() - 0.5;
        double y = Math.random() - 0.5;
        double z = Math.random() - 0.5;
        return new Point3D(x,y,z);
    }

    public static Point3D randomPoint3D(double maxX, double maxY, double maxZ){
        double a = 2 * (Math.random() - 0.5);
        double b = 2 * (Math.random() - 0.5);
        double c = 2 * (Math.random() - 0.5);
        return new Point3D(maxX*a,maxY*b,maxZ*c);


    }
}
