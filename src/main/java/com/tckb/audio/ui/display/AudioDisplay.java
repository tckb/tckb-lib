/*
 * Copyright (C) 2013 tckb < Chandra [dot] Tungathurthi [at] rwth-aachen.de >
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tckb.audio.ui.display;

import com.tckb.audio.part.Label;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author tckb
 */
public abstract class AudioDisplay extends JPanel {

    /**
     *  Default time precision
     */
    protected int defaultTimePrec = 2; 

    /**
     * Type of display
     */
    public static enum TYPE {

        /**
         * Waveform display
        
         */
        WAVEFORM,
        /**
         * Spectrogram display
         */
        SPECTROGRAM
    };

    /**
     * Clears all the labels
     *
     * @return the number of labels cleared
     */
    abstract public int clearAllLabels();

    /**
     * Return all the labels
     *
     * @return the labels as an ArrayList
     */
    abstract public ArrayList<Label> getAllLabels();

    /**
     * Delete a label at a given time position
     *
     * @param pos_sec
     * @return the label title given by {@see Label#getTitle() }
     */
    abstract public String deleteLabelAt(double pos_sec);

    /**
     * Returns the crosshair length
     *
     * @return length in pixels
     */
    abstract public int getCrosshairLen();

    /**
     * Returns the maximum zoom level available
     *
     * @return zoom level
     */
    abstract public double getMaxZoom();

    /**
     * Returns the minimum zoom level available
     *
     * @return zoom level
     */ 
    abstract public double getMinZoom();

    /**
     * Returns the current zoom level
     *
     * @return zoom level
     */
    abstract public double getCurrentZoomLevel();

    /**
     * Returns zoom step
     *
     * @return zoom step
     */
    abstract public int getZoomStep();

    /**
     * Repaints the audio display
     */
    abstract public void refreshDisplay();

    /**
     * Resets the zoom level to the minimum zoom level available
     */
    abstract public void resetZoom();

    /**
     * Sets the crosshair length
     * @param crosshairLen length in pixels
     */
    abstract public void setCrosshairLen(int crosshairLen);

    /**
     * Sets the current position of the cursor
     * @param pos_sec position in seconds
     */
    abstract public void setCursorPos(double pos_sec);

    /**
     * Set a Label at the position with the given title
     * @param title
     * @param pos_sec
     */
    abstract public void setLabelAt(String title, double pos_sec);

    /**
     * Sets the minimum zoom to the display
     * @param minZoom
     */
    abstract public void setMinZoom(double minZoom);

    /**
     * Set the current zoom level
     * @param seconds
     */
    abstract public void setZoomLevel(double seconds);

    /**
     * Sets the current zoom step
     * @param level zoom step in seconds
     */
    abstract public void setZoomStep(int level);

    /**
     * Updates the current crosshair(cursor) position 
     * @param pos_sec
     */
    abstract public void updateCrosshairPosition(double pos_sec);

    /**
     * Zoom in the display according to the defined zoom step
     */
    abstract public void zoomIn();

    /**
     * Zoom out the display according to the defined zoom step
     */
    abstract public void zoomOut();

    /**
     * Toggles the audio display; only meta data is visible
     * @return status 
     */
    abstract public boolean toggleDisplay();

    /**
     * Sets the information of the audio display
     * @param info
     */
    abstract public void setDisplayInfo(String info);

    /**
     * Toggles the display of the labels
     * @return status
     */
    abstract public boolean toggleLabels();

    /**
     * Toggles the display of the information
     * @return status
     */
    abstract public boolean toggleInfo();

    /**
     * Toggles the display of the crosshair(cursor)
     * @return status
     */
    abstract public boolean toggleCrosshair();

    /**
     * Toggles the information of the visible window
     * @return
     */
    abstract public boolean toggleWindowInfo();

    /**
     * Returns the Label which "contains" position (x,y) <br/>
     * 
     * NOTE:<br/>
     * Both x & y are in window coordinate space.
     * @see Label#isInsideLabelBox(int, int)
     * @param x  x coordinate
     * @param y y coordinate
     * @return label; null, if doesn't exists
     */
    abstract public Label getLabelAtXY(int x, int y);

    /**
     * Creates a label at a position (x,y)<br/>
     * 
     * NOTE:<br/>
     * Both x & y are in window coordinate space.
     * @param l label to be created
     * @param x x coordinate
     * @param y y coordinate
     */
    abstract public void setLabelAtXY(Label l, int x, int y);

    /**
     * Highlight the label, if exists
     * @param l label to be highlighted
     */
    abstract public void highLightLabel(Label l);

    /**
     * Displays the cursor a the position (x,y)<br/>
     * 
     * NOTE:<br/>
     * Both x & y are in window coordinate space.
     * @param x
     * @param y
     */
    abstract public void showCursorAt(int x, int y);

    /**
     * Displays the cursor
     * @param show status of the cursor
     */
    abstract public void showCursor(boolean show);

    /**
     * Toggles editing label status
     * @param enable status of editing
     */
    abstract public void editLabels(boolean enable);

    /**
     * Returns the time position of the label<br/>
     * @see Label#getPosAsSample() 
     * @param sampleOfLabel
     * @return label time stamp
     */
    abstract public double getLabelTimeStamp(double sampleOfLabel);

    /**
     *  Returns the time precision of the display
     * @return number of digits after the decimal point
     */
    public int getTimePrecision() {
        return defaultTimePrec;
    }
    /**
     * Sets the time precision of the display
     * @param digits #digits after the decimal point
     */
    public void setTimePrecision(int digits) {
        this.defaultTimePrec = digits;

    }
}
