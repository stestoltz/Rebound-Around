package com.berkscareer.reboundaround;

abstract public class ObjectCircle extends Object {

    //properties for all circles
    protected int radius;
    protected double mass;
    protected double xLoc;
    protected double yLoc;

    public ObjectCircle() {
        super();
    }

    //methods for all circles
    public int getRadius() {
        return radius;
    }
    public double getMass() { return mass; }
    public double getXLoc() { return xLoc; }
    public double getYLoc() { return yLoc; }


}
