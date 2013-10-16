/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.audio.part;

import java.awt.Rectangle;

/**
 *
 * @author tckb
 */
public class Label {

    private String title = "EMPTY_LABEL";
    private double posAsSample = 0;
    private Integer currRedIx = 0;
    private int posX = -1;
    private String text = "EMPTY_LABEL";
    private double sample = 0;
    private Integer currRedix = 0;
    private int x = -1;
    private int horizLen_inPixels = 0;
    private int vertLen_inPixels = 0;
    private int posY = -1;
    private Rectangle boundingBox;
    private boolean clicked = false;

    /**
     * Returns the bounding box around the label
     *
     * @return bounding box
     */
    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    /**
     * Returns if the label is being edited manually
     *
     * @return status
     */
    public boolean isOverride() {
        return clicked;
    }

    /**
     * Sets the override flag
     *
     * @param clicked
     */
    public void setOverride(boolean clicked) {
        this.clicked = clicked;
    }

    public Label() {
        boundingBox = new Rectangle(0, 0, 0, 0);
    }

    /**
     * Returns the length of BB in pixels
     *
     * @return horizontal length
     */
    public int getHorizLen_inPixels() {
        return horizLen_inPixels;

    }

    /**
     * Returns the width of BB in pixels
     *
     * @return vertical length
     */
    public int getVertLen_inPixels() {
        return vertLen_inPixels;
    }

    /**
     * Sets the vertical length of BB in pixels
     *
     * @param vertLen_inPixels
     */
    public void setVertLen_inPixels(int vertLen_inPixels) {
        this.vertLen_inPixels = vertLen_inPixels;
    }

    /**
     * Sets the horizontal length of BB in pixels
     *
     * @param horizLen_inPixels
     */
    public void setHorizLen_inPixels(int horizLen_inPixels) {
        this.horizLen_inPixels = horizLen_inPixels;
    }

    /**
     * Returns the visibility of label
     *
     * @return
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Returns the x-position of top-left corner BB
     *
     * @return
     */
    public int getHorzPixel() {
        return posX;
    }

    /**
     * Sets the x-position of top-left corner BB
     *
     * @param pixel
     */
    public void setHorzPixel(int pixel) {
        this.posX = pixel;
    }

    /**
     * Sets the visibility of label
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    private boolean visible = false;

    /**
     * Creates label with given title at a position given by its sample#
     *
     * @param title
     * @param posAsSample
     */
    public Label(String title, double posAsSample) {
        this.title = title;
        this.posAsSample = posAsSample;
    }

    /**
     * Returns the title of the label
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPosAsSample() {
        return posAsSample;
    }

    public void setPosAsSample(double posAsSample) {
        this.posAsSample = posAsSample;
    }

    /**
     * Returns the current reduction index; useful for waveform display
     *
     * @return
     */
    public Integer getCurrRedIx() {
        return currRedIx;
    }

    public void setCurrRedIx(Integer currRedIx) {
        this.currRedIx = currRedIx;
    }

    /**
     * Creates bounding box around this label
     */
    public void createBoundingBox() {
        boundingBox = new Rectangle(posX, posY, vertLen_inPixels + 2, horizLen_inPixels + 2);
    
    }

    /**
     * Delete the bounding box
     */
    public void deleteLabelBox() {
        boundingBox = new Rectangle(0, 0, 0, 0);
    }

    /**
     * Check if given (x,y) position is inside BB of this label
     *
     * @param x
     * @param y
     * @return true, if contains, else false
     */
    public boolean isInsideLabelBox(int x, int y) {

        return boundingBox.contains(x, y);
    }

    /**
     * Sets the y-position of top-left corner BB
     *
     * @param y
     */
    public void setVertPixel(int y) {
        this.posY = y;

    }

    /**
     *  Returns the y-position of top-left corner BB
     * @return
     */
    public int getVertPixel() {
        return posY;
    }
}
