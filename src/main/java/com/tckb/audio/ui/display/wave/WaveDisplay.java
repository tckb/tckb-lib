package com.tckb.audio.ui.display.wave;

import com.tckb.audio.part.Block;
import com.tckb.audio.part.Block.Reduction;
import com.tckb.audio.part.Label;
import com.tckb.audio.ui.display.AudioDisplay;
import com.tckb.borrowed.jfreechart.ChartColor;
import com.tckb.util.Utility;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ejml.ops.CommonOps;
import org.ejml.simple.SimpleMatrix;

/**
 *
 * @author tckb
 */
public class WaveDisplay extends AudioDisplay {

    private static final Logger mylogger = Logger.getLogger("com.tckb.audio.ui");
    private final int h = 0;
    private WvParams params;
    private double winStart_sample = 0;
    private double winEnd_sample;
    private double currPlay_sample;
    private HashMap<Integer, Reduction> cachRed;
    private HashMap<Integer, Double> cachSamples;
    private double windowSize_sample;
    private double windowSize_sec;
    private int zoomStep;
    private double MAX_ZOOM;
    private double MIN_ZOOM;
    private ArrayList<Label> labels = new ArrayList<Label>();
    private ArrayList<Label> currWinLabel = new ArrayList<Label>();
    private int crosshairLen;
    private int WIN_TIME_HEIGHT = 2;
    private int CURR_TIME_HEIGHT = 2;
    private int LABEL_HEIGHT = 2;
    private final int WIN_MIN_HORPX = 0;
    private final int WIN_MIN_VERPX = 0;
    private int FONT_CHAR_HEIGHT;
    private int WIN_MAX_HORPX = 0;
    private int WIN_MAX_VERPX = 0;
    private int FONT_CHAR_GAP = 0;
    private int WAVE_HEIGHT;
    private boolean displayPixels;
    private boolean displayLabels;
    private boolean displayInfo;
    private boolean displayCrosshair;
    private boolean displayWinInfo;
    private int cursor_x, cursor_y;
    private boolean showCursor = false;
    private boolean editingLabel;

    private void setMAX_ZOOM(double level) {
        mylogger.log(Level.INFO, "Settings max zoom: {0}", level);
        this.MAX_ZOOM = level;


    }

    @Override
    public double getMAX_ZOOM() {
        return MAX_ZOOM;
    }

    @Override
    public double getMIN_ZOOM() {
        return MIN_ZOOM;
    }

    @Override
    public final void setMIN_ZOOM(double level) {
        mylogger.log(Level.INFO, "Settings min zoom: {0}", level);

        this.MIN_ZOOM = level;
    }

    @Override
    public void zoomIn() {
        int step = getZoomStep(); // sec
        mylogger.log(Level.INFO, "Zooming-in {0} level ", step);

        // check if zoomIn available
        if (getZoomLevel() >= getMAX_ZOOM()) {
//            System.out.println("zooming In");

            if ((windowSize_sec - step) >= getMAX_ZOOM()) {
                setZoomLevel(windowSize_sec - step);
            }
//            repaint();
//        }
        } else {
            mylogger.log(Level.WARNING, "Zooming-in level:{0} not available! ", step);

        }
    }

    @Override
    public void zoomOut() {

        int step = getZoomStep();
        mylogger.log(Level.INFO, "Zooming-out {0} level ", step);

        if (getZoomLevel() <= getMIN_ZOOM()) {
//            System.out.println("zooming out");

//            if ((windowSize_sec - step) >= getMIN_ZOOM()) {
            setZoomLevel(windowSize_sec + step);
//            }
//            repaint();

        } else {
            mylogger.log(Level.WARNING, "Zooming-out {0} level  not avaiable", step);

        }



    }

    @Override
    public final void setZoomStep(int level) {
        mylogger.log(Level.INFO, "Setting zoom step level:{0} ", level);

        this.zoomStep = level;

    }

    @Override
    public int getZoomStep() {
        return zoomStep;
    }

    /*
     *  In sec's
     */
    @Override
    public final void setZoomLevel(double seconds) {
//        if (windowSize_sec <= getMAX_ZOOM()) {
        mylogger.log(Level.INFO, "Setting zoom level :{0} seconds ", seconds);

        this.windowSize_sec = seconds;
        this.windowSize_sample = windowSize_sec * params.SRATE;
        winStart_sample = 0;
        winEnd_sample = (params.DUR_SEC > windowSize_sec) ? windowSize_sample : params.DUR_SEC * params.SRATE;

        currWinLabel.clear();




        repaint();
    }

    @Override
    public double getZoomLevel() {
        return windowSize_sec;
    }

    /**
     *
     * @param datab
     * @param wvParams
     */
    public WaveDisplay(Block[] datab, WvParams wvParams) {
        mylogger.info("Initializing wave display");

        params = wvParams;

        setDoubleBuffered(true);
        setBackground(ChartColor.LIGHT_GRAY);
        setZoomStep(1);
        setMIN_ZOOM(params.DUR_SEC);
        setMAX_ZOOM(1);
        if (params.DUR_SEC == 0) {
            windowSize_sec = params.DUR_MS;
        } else {
            windowSize_sec = params.DUR_SEC;
        }
        crosshairLen = 15;
        cachRed = new HashMap<Integer, Reduction>();
        cachSamples = new HashMap<Integer, Double>();
        displayPixels = true;
        displayLabels = true;
        displayInfo = true;
        displayCrosshair = true;
        displayWinInfo = true;

    }

    @Override
    public int getCrosshairLen() {
        return crosshairLen;
    }

    @Override
    public void setCrosshairLen(int crosshairLen) {
        mylogger.log(Level.INFO, "Setting crosshair length to :{0} pixels ", crosshairLen);


        this.crosshairLen = crosshairLen;
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);


        WIN_MAX_HORPX = getWidth();
        WIN_MAX_VERPX = getHeight();
        FONT_CHAR_HEIGHT = g.getFontMetrics().getHeight();
        FONT_CHAR_GAP = getFont().getSize();


        mylogger.fine("Inside paiting component ");
        adjWvParams(getWidth());

        int currPxl = 0;
        int maxPixel = params.PIXEL_COUNT;
        mylogger.log(Level.FINE, "Maximum pixels available: {0}", maxPixel);

        // TODO: Deperecated code!
        if (params.SAMPLE_PER_PIXEL < WvParams.RED_SIZE_SAMPLE) {
            params.SAMPLE_PER_PIXEL = WvParams.RED_SIZE_SAMPLE;
        }
        params.RED_PER_PIXEL = round(params.RED_COUNT, params.PIXEL_COUNT);


        displayPixels((Graphics2D) g, currPxl, maxPixel);
    }

    public int round(int x, int y) {

        int d = x / y;
        int r = x % y;
        //  System.out.println(bb_x + ":" + bb_y + ";" + d + ";" + (r>bb_y/2));
        if (r > (y / 2)) {
            return ++d;
        } else {
            return d;
        }
    }

    private void displayPixels(Graphics2D g, int currPxl, int maxPixel) {
        //   g.clearRect(0, 0, getWidth(), getHeight());
        mylogger.log(Level.INFO, "Painting {0} hor-pixels {1} vert-pixels   ", new Object[]{maxPixel, getHeight()});

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        float w = 0;
        w = 0.5f;
        //        if(windowSize_sec >=2 && windowSize_sec <4){
        //            w=3f;
        //        }if(windowSize_sec <2){
        //            w=1.5f;
        //        }else{
        //            w=4f;
        //        }
        // Stroke definitions
        BasicStroke waveStroke = new BasicStroke(w, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        BasicStroke currPointerStroke = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        BasicStroke labelBBStroke = new BasicStroke(w, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, new float[]{1.0f, 1.0f}, 0.0f); // Dash pattern


        // Paint window start 
        String t1 = Double.toString(Utility.roundDouble(winStart_sample / params.SRATE, timePrecs)) + " sec";
        g.drawString(t1, WIN_MIN_HORPX + 1, WIN_MIN_VERPX + FONT_CHAR_GAP);

        // setup  stroke
        g.setStroke(waveStroke);
        // paint window pointer
        g.draw(new Line2D.Double(WIN_MIN_HORPX + 1, WIN_MIN_VERPX + FONT_CHAR_HEIGHT, WIN_MIN_HORPX + 1, FONT_CHAR_HEIGHT + 3));






        // Paint window End 
        String t = Double.toString(Utility.roundDouble(winEnd_sample / params.SRATE, timePrecs)) + " sec";
        int t_width = g.getFontMetrics().stringWidth(t);
        // paint string
        g.drawString(t, WIN_MAX_HORPX - t_width - 1, WIN_MIN_VERPX + FONT_CHAR_GAP);
        // paint window pointer
        g.draw(new Line2D.Double(WIN_MAX_HORPX - 2, WIN_MIN_VERPX + FONT_CHAR_HEIGHT, WIN_MAX_HORPX - 2, WIN_MIN_VERPX + FONT_CHAR_HEIGHT + 3));





        // Paint display info
        String durInfo = Double.toString(Utility.roundDouble(params.DUR_SEC, timePrecs)) + " sec";
        String cursorInfo;



        if (showCursor) {
//            double curSample = ((winStart_sample + cursor_x) * windowSize_sample) / WIN_MAX_HORPX;
            double curSample = (winStart_sample + cursor_x * (windowSize_sample / WIN_MAX_HORPX));


            cursorInfo = Double.toString(Utility.roundDouble(curSample / params.SRATE, timePrecs)) + " sec";

        } else {
            cursorInfo = " ~ ";

        }

        String resolInfo = Double.toString(Utility.roundDouble(this.windowSize_sec, timePrecs)) + " sec";


        // calc width of the text
        int dinfo_width = g.getFontMetrics().stringWidth(durInfo + cursorInfo + resolInfo) + 5;
        int x = WIN_MAX_HORPX / 2;
        int y = WIN_MAX_VERPX - 2;
        g.setColor(ChartColor.VERY_DARK_BLUE);
        g.drawString("Crsr Pos: " + cursorInfo + " / Total: " + durInfo + " / Showing: " + resolInfo, x - dinfo_width, y);




        int winStart_Red = redNumber(winStart_sample);
        int currPlay_Pxl = 0;
        int mid = interpolate(0, -1, 1, LABEL_HEIGHT, WAVE_HEIGHT);


        List<Reduction> currWinRedList = params.wavData.subList(redNumber(winStart_sample), redNumber(winEnd_sample));


        while (currPxl < maxPixel) {

            g.setColor(new Color(0x40, 0x45, 0xFF));
            g.setStroke(waveStroke);

            int currWinPxl_redstart = (int) Math.floor((winEnd_sample - winStart_sample) * currPxl / (WvParams.RED_SIZE_SAMPLE * maxPixel));
            int currWinPxl_redEnd = (int) Math.floor(((winEnd_sample - winStart_sample) * (currPxl + 1) / (WvParams.RED_SIZE_SAMPLE * maxPixel)));


            double tmin = Integer.MAX_VALUE;
            double tmax = Integer.MIN_VALUE;

            for (int i = currWinPxl_redstart; i < currWinPxl_redEnd; i++) {
                tmax = Math.max(tmax, currWinRedList.get(i).getMax());
                tmin = Math.min(tmin, currWinRedList.get(i).getMin());
            }


            int currPlay_redix = (redNumber(currPlay_sample) - winStart_Red - 1);


            // interpolate tmin & tmax to the current width and height
            int tmin_adj = interpolate(tmin, -1, 1, LABEL_HEIGHT, WAVE_HEIGHT);
            int tmax_adj = interpolate(tmax, -1, 1, LABEL_HEIGHT, WAVE_HEIGHT);

            mylogger.log(Level.FINEST, "Interpolating wavepixels old/new: {0}/{1}, {2}/{3}  ", new Object[]{tmin, tmin_adj, tmax, tmax_adj});
            cachRed.put(currPxl, new Reduction(tmax_adj, tmin_adj));
//            cachSamples.put(currPxl, currPlay_sample);


//          Condition to avoid spurious peaks
            if (tmax_adj - tmin_adj < ((WAVE_HEIGHT - LABEL_HEIGHT - 10) / 2)) {


                // save current sec for ccrosshair pointer
                if (currPlay_redix >= currWinPxl_redstart && currPlay_redix <= currWinPxl_redEnd) {

                    currPlay_Pxl = currPxl;


                }


                // Save the adjusted reductions

                // draw current pixel
                if (displayPixels) {
                    g.setColor(ChartColor.BLUE);
                    g.draw(new Line2D.Double(currPxl, tmin_adj, currPxl, tmax_adj));
                }
                // Paint labels
                if (displayLabels) {
                    for (Label l : labels) {
                        Double sample = l.getSample();
                        int sample_redix = (redNumber(sample) - winStart_Red - 1);


                        // if the label is in  crrent window
                        if (sample_redix >= currWinPxl_redstart && sample_redix <= currWinPxl_redEnd && !l.isVisible()) {


                            int bb_x, bb_y;
                            double samples;
                            if (!l.isOverride()) {
                                bb_x = currPxl;
                                bb_y = CURR_TIME_HEIGHT + 2;
                                samples = l.getSample();
                                tmin_adj = (int) Math.floor(cachRed.get(bb_x).getMin());
                            } // manual edit!
                            else {
                                bb_x = l.getHorzPixel();
                                bb_y = l.getVertPixel();

                                samples = (winStart_sample + bb_x * (windowSize_sample / WIN_MAX_HORPX));
                                l.setSample(samples);
                                l.setOverride(false);


                            }


                            String timeText = Utility.roundDouble(samples / params.SRATE, timePrecs) + " sec";
                            int timeTextLen = g.getFontMetrics().stringWidth(timeText);
                            int labelLen = g.getFontMetrics().stringWidth(l.getText());
                            int horLen = (labelLen >= timeTextLen) ? labelLen : timeTextLen;
                            int vertLen = 2 * FONT_CHAR_HEIGHT + 1;



                            l.setHorizLen_inPixels(horLen + 1);
                            l.setVertLen_inPixels(vertLen);
                            l.setCurrRedix(sample_redix);
                            l.setHorzPixel(bb_x + 6);
                            l.setVertPixel(bb_y - 12);


                            l.createBoudingBox(); // must be called explictly!
//                            l.setOverride(false); // reset!

                            g.setColor(ChartColor.VERY_LIGHT_GREEN);
                            // draw line
                            g.draw(new Line2D.Double(bb_x, bb_y - 7, bb_x, tmin_adj - 1));
                            g.draw(new Line2D.Double(bb_x, bb_y - 7, bb_x + 2, bb_y - 7));

                            // draw label
                            g.setColor(ChartColor.VERY_DARK_MAGENTA);
                            g.drawString(l.getText(), bb_x + 7, bb_y); // label - text
                            g.setColor(ChartColor.VERY_DARK_YELLOW);

                            g.drawString(timeText, bb_x + 7, bb_y + FONT_CHAR_HEIGHT); // label - time


                            // draw bounding box
                            if (editingLabel) {
                                g.setStroke(labelBBStroke);
                                g.drawRect(l.getHorzPixel(), l.getVertPixel(), l.getHorizLen_inPixels(), l.getVertLen_inPixels());
                            }




                            l.setVisible(true);


                            currWinLabel.add(l);



//                    }

                        }

                    }
                }
//
//               


            }

            currPxl++;



            if (showCursor) {
                // horizontal line
                g.setColor(ChartColor.VERY_DARK_GREEN);
                g.draw(new Line2D.Double(cursor_x, LABEL_HEIGHT + 10, cursor_x, WIN_MAX_VERPX - FONT_CHAR_HEIGHT - 2));
                // vertical line
//            g.draw(new Line2D.Double(0, cursor_y, WIN_MAX_HORPX, cursor_y));


            }


        }

        // display crosshair
        mylogger.finest("Painting crosshair");

        int p = currPlay_Pxl - crosshairLen;
        int i = 1;


        while (i <= (2 * crosshairLen) + 1) {
            if ((p + i) > 0 && (p + i) < maxPixel && (p + i) < cachRed.size()) {


                double max = cachRed.get((p + i)).getMax();
                double min = cachRed.get((p + i)).getMin();

                if ((max - min) < ((WAVE_HEIGHT - LABEL_HEIGHT - 10) / 2)) {



                    // crosshair-length 
                    g.setStroke(waveStroke);
                    g.setColor(ChartColor.VERY_DARK_BLUE);
                    g.draw(new Line2D.Double(p + i, min, p + i, max));

                    // crosshair-top
                    g.setColor(ChartColor.DARK_CYAN);
                    g.draw(new Line2D.Double(p + i, max - 0.2, p + i, max + 0.2));
                    g.setColor(ChartColor.DARK_YELLOW);

                    g.draw(new Line2D.Double(p + i, min - 0.2, p + i, min + 0.2));

                    // crosshair time
                    if ((p + i) == currPlay_Pxl) {
                        g.setStroke(currPointerStroke);
                        // draw pulse
                        g.setColor(ChartColor.LIGHT_RED);
                        g.draw(new Line2D.Double((p + i), mid + 0.5, (p + i), mid - 0.5));

                        // draw string
                        g.drawString(Double.toString(Utility.roundDouble(currPlay_sample / params.SRATE, timePrecs)) + " sec", (p + i) - 5, WIN_TIME_HEIGHT + 2);
                        g.setStroke(waveStroke);
                        // draw pointer
                        g.draw(new Line2D.Double((p + i), WIN_TIME_HEIGHT + 2, (p + i), WIN_TIME_HEIGHT + 5));



                    }



                }
            }
            i++;
        }

        // Reset label visibility

        for (Label l : labels) {
            l.setVisible(false);

        }









//        System.out.println("Curr labels:" + currWinLabel.size());

        cachRed.clear();


    }

    public void adjWvParams(int displayWidth) {
        mylogger.info("Adjusting wave constants");

        WIN_TIME_HEIGHT = WIN_MIN_VERPX + FONT_CHAR_GAP + FONT_CHAR_HEIGHT;
        CURR_TIME_HEIGHT = WIN_TIME_HEIGHT + FONT_CHAR_HEIGHT;
        LABEL_HEIGHT = CURR_TIME_HEIGHT + FONT_CHAR_HEIGHT;
        WAVE_HEIGHT = WIN_MAX_VERPX - LABEL_HEIGHT;



        params.PIXEL_COUNT = displayWidth;
        params.TIME_PER_PIXEL = params.DUR_SEC / params.PIXEL_COUNT;
        params.TIME_PER_RED = params.DUR_SEC / WvParams.RED_SIZE_SAMPLE;  // pos_sample per say, 256 samples
        params.TIME_PER_SAMPLE = params.DUR_SEC / params.ADJ_SAMPLE_COUNT; // use the adjusted value instead of original pos_sample count
        params.SAMPLE_PER_PIXEL = params.ADJ_SAMPLE_COUNT / params.PIXEL_COUNT;



        mylogger.log(Level.FINE, "Adjusted Sample count={0}", params.ADJ_SAMPLE_COUNT);
        mylogger.log(Level.FINER, "tpp:{0}", params.DUR_SEC / params.PIXEL_COUNT);


    }

    public int interpolate(double oldValue, double oldRangeMin, double oldRangeMax, double newRangeMin, double newRangeMax) {
        mylogger.log(Level.FINEST, "Interpolating {0} [ {1}, {2} ] -> [ {3}, {4}] ", new Object[]{oldValue, oldValue, oldRangeMax, newRangeMin, newRangeMax});

        int scale = (int) Math.round((newRangeMax - newRangeMin) / (oldRangeMax - oldRangeMin));
        int newValue = (int) Math.round(newRangeMin + (oldValue - oldRangeMin) * scale);

        return newValue;
    }

    public double max(double... listValues) {
        SimpleMatrix list = new SimpleMatrix(1, listValues.length, true, listValues);
        return CommonOps.elementMax(list.getMatrix());
    }

    private double min(double... listValues) {
        SimpleMatrix list = new SimpleMatrix(1, listValues.length, true, listValues);
        return CommonOps.elementMin(list.getMatrix());
    }

    @Override
    public void updateCrosshairPosition(double sec) {

        double pos_sec = Utility.roundDouble(sec, timePrecs);



        mylogger.log(Level.FINER, "Updating crosshair position: {0} sec", pos_sec);
        double pos_sample = pos_sec * params.SRATE;

        if (pos_sample >= winStart_sample && pos_sample < winEnd_sample) {
            mylogger.fine("crosshair position inside current window");


            currPlay_sample = pos_sample;
            repaint();

        } else {
            mylogger.info("crosshair position outside current window");
            double samples = 0;
            currWinLabel.clear();

            if ((windowSize_sample) > (params.SAMPLE_COUNT - winEnd_sample)) {
                if (params.SAMPLE_COUNT - winEnd_sample > 0) {
                    windowSize_sample = params.SAMPLE_COUNT - winEnd_sample;
                } else {
                    mylogger.info("crosshair position Last window reached");
                    samples = 0;
                }

            } else {
                samples = windowSize_sample;
            }

            winStart_sample += samples;
            winEnd_sample += samples;
            currPlay_sample = pos_sample;

            repaint();
        }



    }

    private int redNumber(double sample) {
        return ((int) Math.floor(sample / WvParams.RED_SIZE_SAMPLE));
    }

    @Override
    public void resetZoom() {
        mylogger.info("Resetting zoom ");
        // TODO: Caught bug while resetting the zoom at the after finishing the play
        this.setZoomLevel(getMIN_ZOOM());

//        this.setZoomLevel(params.DUR_SEC);




    }

    @Override
    public void setLabelAt(String text, double pos_sec) {

        mylogger.log(Level.INFO, "Setting label: {0} at sec: {1}", new Object[]{text, pos_sec});


        double pos_sec_adj = Utility.roundDouble(pos_sec, timePrecs);
        String labelText = (text != null) ? text : "UNKOWN_LABEL";


        double pos_sample = pos_sec_adj * params.SRATE;
        labels.add(new Label(labelText, pos_sample));


//        System.out.println("Adding Label: "+ labelText + "  ts: "+ pos_sec_adj);


    }

    /**
     * returns only one label
     *
     * @param pos_sec
     * @return
     */
    @Override
    public String deleteLabelAt(double pos_sec) {

//        double pos_sample = pos_sec * params.SRATE;

        String label = "";
        ArrayList<Label> copy = new ArrayList<Label>(labels);
        for (Label l : copy) {


            double labelTimeAdj = Utility.roundDouble(l.getSample() / params.SRATE, timePrecs);
//            if (l.getSample() == pos_sample) {
//                label = l.getText();
//                labels.remove(l);
//            }

            if (labelTimeAdj == pos_sec) {

                mylogger.log(Level.INFO, "Deleting label: {0} at sec: {1}", new Object[]{l.getText(), labelTimeAdj});
                label = l.getText();
                labels.remove(l);
            }

        }
        copy = null;

        return label;

    }

    @Override
    public int clearAllLabels() {
        mylogger.info("Cleaing all labels");
        int nlabels = labels.size();
        labels.clear();
        return nlabels;

    }

    @Override
    public void refreshDisplay() {
        mylogger.info("Refreshing display");

        repaint();
    }

    public Double getWindowSize_sample() {
        return this.windowSize_sample;
    }

    @Override
    public ArrayList<Label> getAllLabels() {
        return labels;
    }

    @Override
    public void setCursorPos(double sec) {

        double pos_sec = Utility.roundDouble(sec, timePrecs);

        if (pos_sec >= params.DUR_SEC) {
            pos_sec = params.DUR_SEC;
        } else {
            if (pos_sec < 0) {
                pos_sec = 0;
            }
        }

        double pos_sample = pos_sec * params.SRATE;
        winStart_sample = (0 > (pos_sample - windowSize_sample * 0.5)) ? 0 : (pos_sample - windowSize_sample * 0.5);
        winEnd_sample = winStart_sample + windowSize_sample;
        currPlay_sample = pos_sample;

        repaint();

    }

    @Override
    public boolean toggleDisplay() {
        displayPixels = !displayPixels;
        repaint();
        return displayPixels;
    }

    @Override
    public void setDisplayInfo(String info) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean toggleLabels() {
        displayLabels = !displayLabels;
        repaint();
        return displayLabels;
    }

    @Override
    public boolean toggleInfo() {
        displayInfo = !displayInfo;
        repaint();
        return displayInfo;
    }

    @Override
    public boolean toggleCrosshair() {
        displayCrosshair = !displayCrosshair;
        repaint();
        return displayCrosshair;
    }

    @Override
    public boolean toggleWindowInfo() {
        displayWinInfo = !displayWinInfo;
        repaint();
        return displayWinInfo;
    }

    @Override
    public Label getLabelAtXY(int x, int y) {
        for (Label l : currWinLabel) {
            // restrict 'Y'
//            System.out.println("bb_x:" + bb_x + "bb_y:" + bb_y + " l.pixel" + l.getHorzPixel() + " len: " + l.getLength_in_pixels());
            if (l.isInsideLabelBox(x, y)) {
                return l;
            }
        }

        return null;

    }

    @Override
    public void highLightLabel(Label l) {
    }

    @Override
    public void setLabelAtXY(Label l, int x, int y) {
        if (editingLabel) {
            l.setOverride(true); // just a flag
            l.setHorzPixel(x);
            l.setVertPixel(y);
            repaint();

        }


    }

    @Override
    public void showCursorAt(int x, int y) {
        this.cursor_x = x;
        this.cursor_y = y;
        repaint();

    }

    @Override
    public void showCursor(boolean show) {
        this.showCursor = show;
        repaint();
    }

    @Override
    public void editLabels(boolean b) {
        this.editingLabel = b;
        repaint();
    }

    @Override
    public double getLabelTimeStamp(double sampleOfLabel) {
        return Utility.roundDouble(sampleOfLabel / params.SRATE, timePrecs);
    }
}
